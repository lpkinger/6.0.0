Ext.define('erp.view.plm.request.PreProject',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	initComponent : function(){ 
		var me = this; 
		if(formCondition!=''&&(!gridCondition||gridCondition=='')){
			me.getUrl(formCondition);
		}else{	
			gridCondition='';
			Ext.apply(me, { 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					deleteUrl: 'plm/request/deletePreProject.action',
					updateUrl: 'plm/request/updatePreProject.action',
					submitUrl: 'plm/request/submitPreProject.action',
					resSubmitUrl: 'plm/request/resSubmitPreProject.action',
					auditUrl: 'plm/request/auditPreProject.action',
					resAuditUrl: 'plm/request/resAuditPreProject.action',
					turnProjectUrl: 'plm/request/turnToProject.action',
					keyField: 'pp_id',
					codeField: 'pp_code',
					statusField: 'pp_status',
					statuscodeField: 'pp_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'ppd_detno',
					keyField: 'ppd_id',
					mainField: 'ppd_ppid',
					allowExtraButtons: true
				}]
			}); 
		}
		me.callParent(arguments); 
	},
	getUrl:function(formCondition){
		Ext.Ajax.request({
			url : basePath + 'plm/request/getID.action',
			params: {formCondition:formCondition},
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.success){
					var id = res.Id;
					formCondition = 'pp_idIS'+id;
					gridCondition = 'ppd_ppidIS'+id;
					window.location.href = window.location.href.substring(0,window.location.href.lastIndexOf('?')) + '?formCondition=' + 
					formCondition + '&gridCondition=' + gridCondition;
				} else if(res.exceptionInfo){					
					showError(res.exceptionInfo);
				} 
			}
		})
	} 
});