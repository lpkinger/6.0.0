Ext.QuickTips.init();
Ext.define('erp.controller.hr.employee.JobPower', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','hr.employee.JobPower','core.grid.JobPowerTreeGrid',
      		'core.button.Delete','core.button.Close','core.button.Update',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField'
      	],
    init:function(){
    	var me = this;
    	this.control({
    		'erpJobPowerTreeGrid': { 
    			itemmousedown: this.onGridItemClick,
    			itemclick: this.onGridItemClick
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(){
    				me.onUpdate();
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	var treegrid = Ext.getCmp('treegrid');
    	if (!record.get('leaf')) {
    		if(record.isExpanded() && record.childNodes.length > 0){//是根节点，且已展开
				record.collapse(true,true);//收拢
			} else {//未展开
				//看是否加载了其children
				if(record.childNodes.length == 0){
					//从后台加载
					treegrid.getTreeGridNode(record.data['id']);
				} else {
					record.expand(false,true);//展开
				}
			}
    	}
    },
    onUpdate: function(){
    	var me = this;
    	var grid = Ext.getCmp('treegrid');
		grid.getUpdateNodes();
		var update = new Array();
		var index = 0;
		Ext.each(grid.updateNodes, function(){
			var o = {
					pp_unposting: this.data.pp_unposting ? 1 : 0,
    				pp_posting: this.data.pp_posting ? 1 : 0,
    				pp_unclosed: this.data.pp_unclosed ? 1 : 0,
    				pp_closed: this.data.pp_closed ? 1 : 0,
    				pp_undisable: this.data.pp_undisable ? 1 : 0,
    				pp_disable: this.data.pp_disable ? 1 : 0,
    				pp_print: this.data.pp_print ? 1 : 0,
    				pp_unaudit: this.data.pp_unaudit ? 1 : 0,
    				pp_audit: this.data.pp_audit ? 1 : 0,
    				pp_uncommit: this.data.pp_uncommit ? 1 : 0,
    				pp_commit: this.data.pp_commit ? 1 : 0,
    				pp_save: this.data.pp_save ? 1 : 0,
    				pp_delete: this.data.pp_delete ? 1 : 0,
    				pp_add: this.data.pp_add ? 1 : 0,
    				pp_seeall: this.data.pp_seeall ? 1 : 0,
    				pp_see: this.data.pp_see ? 1 : 0,
    				pp_poid: this.data.pp_poid,
    				pp_joid: this.data.pp_joid,
    				pp_id: this.data.pp_id
			};
			update[index++] = Ext.JSON.encode(o);
		});
		if(update.length > 0){
			var activeTab = me.BaseUtil.getActiveTab();
			activeTab.setLoading(true);
			Ext.Ajax.request({
	        	url : basePath + 'hr/employee/updateJobPower.action',
	        	params: {
	        		update: unescape(update.toString().replace(/\\/g,"%"))
	        	},
	        	callback : function(options,success,response){
	        		var res = new Ext.decode(response.responseText);
	        		activeTab.setLoading(false);
	        		if(res.success){
	        			grid.updateNodes = [];
	        			updateSuccess(function(){
	        				grid.getTreeGridNode(0);
	        			});
	        		} else if(res.exceptionInfo){
	        			showError(res.exceptionInfo);
	        		}
	        	}
	        });
		}
    }
});