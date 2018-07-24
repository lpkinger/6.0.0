
Ext.define('erp.view.common.JProcess.JProcessAutoAudit.NewrRequireApply',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.NewrRequireApply',
	id: 'newrrequireapply', 
	autoScroll:false,
	closeAction:'hide',
	layout: "column",
	cls:'newrrequireapply',
	items:[
	{
		bodyStyle:'border:none;background: #f1f1f1',
		style:'background: #f1f1f1',
		//anchor:'100% 30%',
		height:25,
		columnWidth: 1,
		layout:'hbox',
		items:[
		{
			xtype:'container',
			flex:13,
			height:10
		},
		{
			xtype:'button',
			flex:1,
			text:'确认',
			height:20,
			id:'newrequireapplybtn',
			handler:function(){
				var applytext=Ext.getCmp("applytext").value;
				if(applytext==null){						
					Ext.Msg.alert("提示", "需求内容不能为空!");	 
				}else{
					this.ownerCt.ownerCt.sureNewApply();
				}
			}	
		}]
		
			
	},	
	{
		xtype:'textarea',
		id:'applytext',
		emptyText:'需求内容',
		//anchor:'100% 70%',
		height:100,
		columnWidth: 1,
		
	}],
	
	initComponent : function(){ 	
		this.callParent(arguments);
		
		
	},

	sureNewApply:function(){
		Ext.MessageBox.show({
	        title: "申请原因",
	        width: 300,
	        buttons: Ext.MessageBox.OKCANCEL,
	        multiline: true,
	        fn: this.saveNewApply    
	    });
		
	},
	saveNewApply:function(btn,text){
		console.log("saveNewApply");
		//这里需要单据caller、流程名称Ra_processname、节点名称Ra_nodename
		//applytext:需求内容            text:申请原因 
		var nodename, processname, caller;
		var select=Ext.getCmp("otherrulesgrid").getSelectionModel().getLastSelected();
		console.log(select);
		if(select!=null){
			 nodename=select.data.JT_NAME;
			 processname=select.data.JD_PROCESSDEFINITIONID;
			 caller=select.data.JD_CALLER;
		}else{
			var flowbody=parent.Ext.getCmp('flowbody');
		 	nodename=flowbody.currentnode;
		 	processname= flowbody.processtitle;
		 	caller=flowbody.caller;
		}
		
		
		if(btn=='ok'){
			var applytext= Ext.getCmp("applytext").value;
			console.log(applytext);
			Ext.Ajax.request({
				url: basePath + 'common/saveNewApply.action',
				params: {
					applytext:applytext,
					text:text,
					caller:caller,
					processname:processname,
					nodename:nodename,					
					_noc: 1
				},
				callback: function(options, success, response) {
					var response = Ext.decode(response.responseText);
					if(response.success){
						var url="jsps/common/jprocessDeal/jprocessRulesApply.jsp?whoami=JprocessRulesApply&formCondition=ra_idIS"+response.id+'&_noc=1';
						showMessage('已转 规则申请单，单据号:<a href="javascript:openUrl(\''+url+'\')">'+response.code+'</a>');
					}else{
						Ext.Msg.alert("提示", response.exceptionInfo);	
					}
					
				}
			});
		}
		
	}
	
	
	
	
});