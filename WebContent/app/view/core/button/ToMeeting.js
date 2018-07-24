/**
 * 转会议申请按钮
 */
Ext.define('erp.view.core.button.ToMeeting', {
	extend : 'Ext.Button',
	alias : 'widget.erpToMeetingButton',
	text : $I18N.common.button.erpToMeetingButton,
	iconCls : 'x-button-icon-submit',
	cls : 'x-btn-gray',
	width : 100,
	style : {
		marginLeft : '10px'
	},
	initComponent : function() {
		this.callParent(arguments);
	},
	FormUtil: Ext.create('erp.util.FormUtil'),
	handler : function(btn) {
		var me = this;
		warnMsg('确定要转会议申请吗？', function(btnMsg){
			if(btnMsg == 'yes'){
				var params =  new Object();
				params.caller = caller;
				params.id = Ext.getCmp(btn.ownerCt.ownerCt.keyField).value;
				Ext.Ajax.request({
					url : basePath + 'plm/team/teamToMeeting.action',
					params: params,
					method : 'post',
					timeout: 6000000,
					callback : function(options,success,response){
						var localJson = new Ext.decode(response.responseText);
					   	if(localJson.exceptionInfo){
					   		var str = localJson.exceptionInfo;
					   		showError(str);
					   		if(!localJson.success){   //出现警告：不能同时存在非审核的会议申请   时，还会跳转到对应的会议申请单
					   			return;
					   		}
					   	}
					   	me.FormUtil.onAdd('teamToMenting_'+localJson.id+'', '会议申请', localJson.url);
					}
				});
			}
		});
	}
});