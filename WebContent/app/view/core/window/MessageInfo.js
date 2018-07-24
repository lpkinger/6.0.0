/**
 * 右下角消息提醒信息条数
 */
Ext.define('erp.view.core.window.MessageInfo', {
	extend: 'Ext.window.Window',
	alias: 'widget.messageinfo',
	timeid:'',
	cls:'messageinfo',
	draggable:false,
	closable: true,
	title:'<div><img style="vertical-align:middle" src="'+basePath+'resource/images/messagetitle.png" height="17" width="17" /><span>&nbsp消息</span><div>',
	bodyStyle: {
		background:'#f2f2f2',
   	 	padding: '5px,5px,5px,5px'
	},
	border:false,
	renderTo: Ext.getBody(),
	x: Ext.getBody().getWidth()-400, 
	y: Ext.getBody().getHeight()-100,
	autoClose: true,
	initComponent: function() {
		var me=this;
		this.id='messageinfo'+this.timeid;
		this.callParent(arguments);					
		this.setPosition(Ext.getBody().getWidth()-340,Ext.getBody().getHeight()-175);	
	},

});