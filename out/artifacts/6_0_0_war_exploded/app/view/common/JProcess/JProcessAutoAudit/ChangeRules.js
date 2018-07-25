
Ext.define('erp.view.common.JProcess.JProcessAutoAudit.ChangeRules',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.ChangeRules',
	id: 'changerules', 
	closeAction:'hide',
	items:[
			{
				xtype:'grid',
				id:'rulegrid',
				height:205,
				showBtn:true,
				autoScroll:true,				
				columns:[	
					  { header: '规则id', dataIndex: 'RU_ID', hidden :true },
					  { header: 'caller', dataIndex: 'CALLER', hidden :true },
					  { header: '规则名称',  dataIndex: 'RU_NAME' ,flex: 2},
		        	  { header: '规则描述', dataIndex: 'RU_DESC', flex:4 },
		        	  { header: '状态', dataIndex: 'RA_STATUS', flex: 0.5 ,
		        	  	renderer:function(val,etc,record){  
		        	  		if(val=='已提交'){
		        	  			return '审核中'
		        	  		}else if(val=='已审核'){
		        	  			return '已启用' 
		        	  		}else{
		        	  			return val;
		        	  		}	
		        	  	}
		        	  
		        	  },
		        	  { header: '操作', dataIndex: 'RA_STATUSCODE', flex: 0.5 ,
		        	  	renderer:function(val,etc,record){   
    	  					var ruid=record.data.RU_ID;
    	  					var showBtn=Ext.getCmp("rulegrid").showBtn;
    	  					if(showBtn==false){
    	  						return '';
    	  					}    
    	  					else if(val=='AUDITED'){   
    	  						return "<button  style='cursor:pointer' title='规则禁用' id='disablebtn' onclick=disableClick("+ruid+")></button>"	   	  					
    	  					}else if(val==""){
    	  						return  "<button  style='cursor:pointer' title='规则申请' id='applybtn' onclick='applyClick("+Ext.encode(record.data)+")'></button>"  								
    	  					}
    	  				}  
		        	  }	        		        	    
				],
			 	store :Ext.create('Ext.data.Store', {
			 			fields: [
			 					 {name: 'RU_ID', type: 'int'},
			 					 {name: 'RU_NAME', type: 'string'},			 					
								 {name: 'RU_DESC', type: 'string'},
								 {name: 'RA_STATUS', type: 'string'},
								 {name: 'RA_STATUSCODE', type: 'string'},
								 {name: 'RA_ID', type: 'string'},
								 {name: 'caller', type: 'string'}
							 	 ]
						
			 			})	
			}			
				],
	
	initComponent : function(){ 	
		this.callParent(arguments);
		
		
	}

});
var disableClick=function(id){
	console.log('禁用');
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
	Ext.Ajax.request({
			url: basePath + 'common/disableRules.action',
			params: {
				id:id,
				nodename:nodename,
				processname:processname,
				caller:caller,
				_noc: 1
			},
			callback: function(options, success, response) {
				var response = Ext.decode(response.responseText);
				if(response.success){
					//alert("规则更改申请成功！");
					var url="jsps/common/jprocessDeal/jprocessRulesApply.jsp?whoami=JprocessRulesApply&formCondition=ra_idIS"+response.id;
					showMessage('已转 规则申请单，单据号:<a href="javascript:openUrl(\''+url+'\')">'+response.code+'</a>');
					var otherrulesgrid=Ext.getCmp("otherrulesgrid");
					otherrulesgrid.getStore().load();
					var rulegrid=Ext.getCmp("rulegrid");
					rulegrid.fireEvent("afterrender",rulegrid);
				}else{		
					Ext.Msg.alert("提示", response.exceptionInfo);	
				}
			}
		});
}

var applyClick=function(record){

		Ext.MessageBox.show({
	        title: "申请原因",
	        width: 300,
	        buttons: Ext.MessageBox.OKCANCEL,
	        multiline: true,
	        record:record,
	        fn: this.changeRulesApply    
	    });

	
	
}

var changeRulesApply=function(btn,text,opt){
	if(btn=='ok'){
		var record=opt.record;
		var nodename, processname, caller;
		var select=Ext.getCmp("otherrulesgrid").getSelectionModel().getLastSelected();
		var id=record.RU_ID;
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
		Ext.Ajax.request({
				url: basePath + 'common/changeRules.action',
				params: {
					id:id,
					text:text,
					nodename:nodename,
					processname:processname,
					caller:caller,
					_noc: 1
				},
				callback: function(options, success, response) {
					var response = Ext.decode(response.responseText);
					if(response.success){
						//alert("规则更改申请成功！");	
						//parent.parent.showMessage('已转 规则申请单，单据号:<a href="javascript:linkToApply(\''+response.id+'\')">'+response.code+'</a>');
						var url="jsps/common/jprocessDeal/jprocessRulesApply.jsp?whoami=JprocessRulesApply&formCondition=ra_idIS"+response.id;
						showMessage('已转 规则申请单，单据号:<a href="javascript:openUrl(\''+url+'\')">'+response.code+'</a>');
						var otherrulesgrid=Ext.getCmp("otherrulesgrid");
						otherrulesgrid.getStore().load();
						var rulegrid=Ext.getCmp("rulegrid");
						rulegrid.fireEvent("afterrender",rulegrid);
						}else{		
						Ext.Msg.alert("提示", response.exceptionInfo);	
					}
				}
			});
		}

	}