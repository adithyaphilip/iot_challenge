<?php
class Pulse extends CI_Model{
	public function __construct()
	{
		parent::__construct();
		$this->load->database();
		$this->load->model('Save');
	}
	/**
	 * params needs 'id'
	 * returns whether their help is required or not
	 */
	function pulse($params)
	{
		
		$this->routine_activities();
		$req = array('id'=>$params['id']);
		$response = $this->db->get_where('emergencies',array('aid'=>$req['id']))->result_array();
		
		if(count($response)!=0)
		{
			$eresponse = $this->db->get_where('userlist',array('id'=>$response[0]['uid']))->result_array();
			return array('emergency'=>'true','user'=>$eresponse[0])	;
		}
		else {
			return array('emergency'=>'false');
		}
	}
	/**
	 * params need 'lx','ly','id'
	 * returns true
	 */
	function locationPulse($params)
	{
		$data = array('lx'=>$params['lx'],'ly'=>$params['ly'],'id'=>$params['id']);
		$this->db->where('id',$params['id'])->update('userlist',$data);
		return true;
	}
	public function routine_activities()
	{
		//$this->db->delete('emergencies',array('state'=>3));
		$this->Save->setUnresponding();
		$response = $this->db->get('emergencies')->result_array();
		foreach($response as $emergency)
		{
			$this->Save->setNewSaviours(array('uid'=>$emergency['uid']));
		}
	}
}
