Ext.QuickTips.init();
Ext.define('erp.controller.ma.CopyConfig', {
    extend:'Ext.app.Controller',
    GridUtil: Ext.create('erp.util.GridUtil'),
	views:[
		'ma.copy.CopyConfig',
		'ma.copy.Form',
		'ma.copy.GridPanel',
		'core.button.Update',
		'core.button.Close',
		'core.button.DeleteDetail',
		'core.trigger.DbfindTrigger'
	],
	init:function(){
		var me = this;
		this.control({
            'erpCopyGridPanel': {
                itemclick: me.onGridItemClick
            },
			'erpUpdateButton': {
                click:me.onUpdate
            },
            'erpDeleteDetailButton':{
    			click:me.ondelete
            }
		});
	},
	ondelete:function(btn){
		var me = this;
		var grid = btn.grid ||btn.ownerCt.ownerCt;
		//解决针对两个从表无法控制多个从表的权限
		var records = grid.selModel.getSelection();
		var condition = "cc_caller='"+Ext.getCmp('cc_caller').value+"' AND cc_field='"+records[0].data['cc_field']+"' AND cc_findkind='"+records[0].data['cc_findkind']+"'";
		if(records.length > 0){
			warnMsg($I18N.common.msg.ask_del, function(btn){
				if(btn == 'yes'){
					Ext.Ajax.request({
				   		url : basePath + "/ma/setting/deleteCopyConfigs.action",
				   		params: {
				   			condition:condition
				   		}, 
				   		method : 'post',
				   		callback : function(options,success,response){
				   			var localJson = new Ext.decode(response.responseText);
				   			if(localJson.exceptionInfo){
			        			showError(localJson.exceptionInfo);return;
			        		}
			    			if(localJson.success){
			    				grid.store.remove(records[0]);
				   				delSuccess(function(){
								});//@i18n/i18n.js
				   			} else {
				   				delFailure();
				   			}
				   		}
					});
				}
			});
		}
	}
	,
    onUpdate: function() {
        //更新
    	var grid = Ext.getCmp('grid');
    	var param = this.GridUtil.getGridStore();
    	if(grid.necessaryField.length > 0 && (param == null || param == '')){
				showError('明细表还未添加数据');return;
		}
		Ext.Ajax.request({
	   		url : basePath + '/ma/setting/updateCopyConfigs.action',
	   		params:{
					gridStore:param,
					caller:caller,
					formCaller:Ext.getCmp('cc_caller').value
			},
	   		method : 'post',
	   		callback : function(options,success,response){
	   			var localJson = new Ext.decode(response.responseText);
	   			if(localJson.exceptionInfo){
        			showError(localJson.exceptionInfo);return;
        		}
	   			if(localJson.success){
					showMessage('提示', '更新成功!', 1000);
					//update成功后刷新页面进入可编辑的页面
					var u = String(window.location.href);
					if (u.indexOf('formCondition') == -1) {
						var value = r[form.keyField];
						var formCondition = form.keyField + "IS" + value ;
						var gridCondition = '';
						var grid = Ext.getCmp('grid');
						if(grid && grid.mainField){
							gridCondition = grid.mainField + "IS" + value;
						}
						if(me.contains(window.location.href, '?', true)){
							window.location.href = window.location.href + '&formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						} else {
							window.location.href = window.location.href + '?formCondition=' + 
							formCondition + '&gridCondition=' + gridCondition;
						}
					} else {
						window.location.reload();
					}
				}
	   		}
		});
    },
    onGridItemClick: function(selModel, record) { //grid行选择
        this.GridUtil.onGridItemClick(selModel, record);
    }
});