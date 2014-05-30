<?php
class Save extends CI_Model{
	public $user_limit = 1;//number of users to be called at a time
	public $time_limit = 30;
	function __construct()
	{
		parent::__construct();
		$this->load->database();
		$this->load->library('paramchecker');
	}
	/**
	 * params requires 'ly',lx','id' 
	 * returns array of users
	 */
	public function getClosestUsers($params)
	{
		$id=$params['id'];
		$lx= $params['lx'];
		$ly=$params['ly'];
		$user_response = $this->db->where('id != ',$id,FALSE)->get('userlist')->result_array();
		$data['l1x']=$lx;
		$data['l1y']=$ly;
		$this->db->where('id',$id)->update('userlist',array('lx'=>$lx,'ly'=>$ly));
		$data['l2x']=$user_response[0]['lx'];
		$data['l2y']=$user_response[0]['ly'];
		
		
		$inresult = array('id','lx','ly');
		$sortguide = array();
		
		for($i=0;$i<count($user_response);$i++)
		{
			$user = $user_response[$i];
			$data['l2x']=$user['lx'];
			$data['l2y']=$user['ly'];
			$user_response[$i]['distance']=$this->distance($data);
			$sortguide[] = $user_response[$i]['distance'];
		}
		array_multisort($sortguide,$user_response);
		$result = array();
		for($i=0;$i<count($user_response)&&$i<$this->user_limit;$i++)
		{
			$result[]=$user_response[$i];
		}
		return $result;
	}
	/**
	 * params requires 'ly',lx','id',and an 'exclude' array which contains ids of excluded users
	 * optionally, a 'cusers' int may be included which determines number of users already present
	 */
	public function getClosestUsersExcluding($params)
	{
		$id=$params['id'];
		$lx= $params['lx'];
		$ly=$params['ly'];
		$this->db->where('id != ',$id);
		$exclude = $params['exclude'];
		foreach($exclude as $exid)
		{
			$this->db->where('id !=',$exid);
		}
		$user_response = $this->db->get('userlist')->result_array();
		if(count($user_response)==0)
			return ;
		$data['l1x']=$lx;
		$data['l1y']=$ly;
		$this->db->where('id',$id)->update('userlist',array('lx'=>$lx,'ly'=>$ly));
		$data['l2x']=$user_response[0]['lx'];
		$data['l2y']=$user_response[0]['ly'];
		
		
		$inresult = array('id','lx','ly');
		$sortguide = array();
		
		for($i=0;$i<count($user_response);$i++)
		{
			$user = $user_response[$i];
			$data['l2x']=$user['lx'];
			$data['l2y']=$user['ly'];
			$user_response[$i]['distance']=$this->distance($data);
			$sortguide[] = $user_response[$i]['distance'];
		}
		array_multisort($sortguide,$user_response);
		$result = array();
		if(!isset($params['cusers']))
		{
			$params['cusers']=0;
		}
		for($i=0;$i<count($user_response)&&$i<($this->user_limit-$params['cusers']);$i++)
		{
			$result[]=$user_response[$i];
		}
		return $result;
	}

	/**
	 * starting of an emergency
	 * requires id (userid), lx,ly params
	 */
	public function initiateEmergency($params)
	{
		$req = array('id','lx','ly');
		if($this->paramchecker->isParamsSet($req,$params))
		{
			$this->db->where('id',$params['id'])->update('userlist',array('lx'=>$params['lx'],'ly'=>$params['ly']));
			$users = $this->getClosestUsers($params);
			$aids = array();
			foreach($users as $user)
			{
				$aids[] = $user['id'];
			}
			$data = array('uid'=>$params['id'],'aids'=>$aids,'state'=>0,'time'=>$this->getTime());
			$this->load->model('Emergencies');
			$this->Emergencies->newEmergency($data);
		}	
		else{
			throw new Exception('id, lx or ly params not set');
		}	
	}
	/**
	 * accepting to assist in an emergency
	 * params requires uid, aid
	 */
	 public function acceptEmergency($params)
	 {
	 	$req = array('uid','aid');
		if($this->paramchecker->isParamsSet($req,$params))
		{
			$data['state']=1;
			foreach($req as $key)
			{
				$data[$key]=$param[$key];
			}
			$this->load->model('Emergencies');
			$this->Emergencies->updateEmergencyState($data);
		}
		else{
			throw new Exception('One of following not set : '.implode(',', $req));
		}
	 }
	 /**
	  * params 'uid', 'aid'
	  */
	 public function rejectEmergency($params)
	 {
	 	$req = array('uid','aid');
		if($this->paramchecker->isParamsSet($req,$params))
		{
			$data['state']=3;
			foreach($req as $key)
			{
				$data[$key]=$param[$key];
			}
			$this->load->model('Emergencies');
			$this->Emergencies->updateEmergencyState($data);
			$this->loadNextUsers();
		}
		else{
			throw new Exception('One of following not set : '.implode(',', $req));
		}
	 }
	 
	/**
	 * removes aids whose last state was sent (0) and was updated time_limit seconds ago
	 */
	public function setUnresponding()
	{
		$where = array('state'=>0,'time < '=>time()-$this->time_limit);
		$this->load->model('Emergencies');
		$this->Emergencies->updateEmergencyState(array('where'=>$where,'state'=>2));
	}
	/**
	 * checks if an emergency has minimum saviors assigned to it or not
	 * params takes 'uid' parameter
	 *  	 
	 * */
	public function setNewSaviours($params)
	{
		$users = array();
		if(isset($params['uid']))
		{
			$users = $this->db->get_where('emergencies',array('uid'=>$params['uid']))->result_array();
			$params['id']=$users[0]['id'];
		}
		if(!isset($params['uid'])){
			throw new Exception('Insufficient parameters');
		}
		$assigned_users = count($users)-$this->countDeclinedUsers($users);
		if($assigned_users<$this->user_limit)
		{
			$exids = array();
			foreach($users as $user)
			{
				$exids[] = $user['aid'];
			}
			$tresponse = $this->db->get_where('userlist',array('id'=>$params['uid']))->result_array();
			$tuser = $tresponse[0];
			
			$musers = $this->getClosestUsersExcluding(array('cusers'=>$assigned_users,'id'=>$params['uid'],'exclude'=>$exids,'lx'=>$tuser['lx'],'ly'=>$tuser['ly']));
			if(count($musers)==0)
			return ;
			$aids = array();
			foreach($musers as $muser)
			{
				$aids[] = $muser['id'];
			}
			$data = array('uid'=>$params['id'],'aids'=>$aids,'state'=>0,'time'=>$this->getTime());
			$this->load->model('Emergencies');
			$this->Emergencies->newEmergency($data);
		}
	}
	/**
	 * meant to be used with setNewSaviours
	 * params contains a 'users' array which is list of all people with aids corresponding to that emergency, in emergency format
	 * returns array of ids of people who have declined and therefore should be excluded
	 */
	public function getExcludedIds($params)
	{
		$ids = array();
		if(!isset($params['users']))
		{
			throw new Exception('Insufficient parameters');
		}
		$users = $params['users'];
		foreach($users as $users){
			if($users['state']==2||$users['state']==3)
			{
				$ids[] = $users['aid'];
			}
		}
		return $ids;
	}
	/**
	 * for use in emergencies format
	 */
	public function countDeclinedUsers($users)
	{
		$ctr = 0;
		//echo '<br>','Declined users - total user count:',count($users),'<br>';
		for($i=0;$i<count($users);$i++)
		{
			if($users[$i]['state']==2||$users[$i]['state']==3)
			{
				$ctr++;
			}
		}
		//echo '<br>','Declined users:',$ctr,'<br>';
		return $ctr;	
	}
	
	public function removeDeclined()
	{
		$where = array('state'=>3);
		$this->load->model('Emergencies');
		$this->Emergencies->deleteEmergency(array('where'=>$where));
	}
	/**
	 * 'l1x', 'l1y', 'l2x', l2y' parameters
	 * returns distance directly
	 */
	function distance($params) {

		$lon1=$params['l1y'];
		$lon2=$params['l2y'];
		$lat1=$params['l1x'];
		$lat2=$params['l2x'];
  		$theta = $lon1 - $lon2;
  		$dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) +  cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
  		$dist = acos($dist);
  		$dist = rad2deg($dist);
		$miles = $dist * 60 * 1.1515;			
		return ($miles * 1.609344);
	}
	
	function getTime()//TEST
	{
		return time();
	}
}
?>