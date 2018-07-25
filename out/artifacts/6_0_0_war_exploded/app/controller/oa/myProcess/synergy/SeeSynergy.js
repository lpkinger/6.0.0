Ext.QuickTips.init();
Ext.define('erp.controller.oa.myProcess.synergy.SeeSynergy', {
    extend: 'Ext.app.Controller',
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    FormUtil: Ext.create('erp.util.FormUtil'),
    views:[
    		'oa.myProcess.synergy.SeeSynergy','oa.myProcess.synergy.SeeSynergyForm'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'button[id=close]': {
    			click: function(){
    				me.FormUtil.onClose();
    			}
    		},
    		'button[id=delete]': {
    			click: function(){
    				Ext.Ajax.request({//拿到grid的columns
    					url : basePath + 'oa/myProcess/synergy/deleteSynergy.action',
    					params: {
    						ids : getUrlParam('id')
    					},
    					method : 'post',
    					async: false,
    					callback : function(options, success, response){
//    						parent.Ext.getCmp("content-panel").getActiveTab().setLoading(false);
    						var res = new Ext.decode(response.responseText);
    						if(res.exceptionInfo){
    							showError(res.exceptionInfo);return;
    						}
    						if(res.success){
    							alert(' 删除成功！');
    						}
    					}
    				});											
    				url = "oa/myProcess/synergy/getSynergyList.action";
//    				console.log(parent.Ext.getCmp('querygrid'));
    				parent.Ext.getCmp('querygrid').getGroupData();
    				me.FormUtil.onClose();
    			}
    		}
    	});
    }
});