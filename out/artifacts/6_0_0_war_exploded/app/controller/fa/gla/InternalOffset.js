Ext.QuickTips.init();
Ext.define('erp.controller.fa.gla.InternalOffset', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','fa.gla.InternalOffset','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Update','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.form.YnField','core.form.ColorField'
      	],
    init:function(){
    	var me = this;
    	me.gridLastSelected = null;
    	this.control({
    		'erpFormPanel':{
    			afterload:function(form){
    				if(mastercode){
    					parent.Ext.getCmp(mastercode).on('activate',function(panel, reload){
    						me.setData(form, reload);
    					});
	    			}
    				me.setData(form, false);
    			}
    		},
    		'erpGridPanel2': { 
    			itemclick: this.onGridItemClick
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.FormUtil.onUpdate(this);
    			}
    		}
    	});
    }, 
    onGridItemClick: function(selModel, record){//grid行选择
    	this.GridUtil.onGridItemClick(selModel, record);
    },
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	setData:function(form, reload){
		var me = this;
		var yearmonth1 = parent.Ext.getCmp('yearmonth').value;
		
		if(yearmonth1!=yearmonth || reload){
			me.FormUtil.setLoading(true);
			var fields = '';
			Ext.Array.each(form.items.items,function(field){
				fields += ',' + field.name;
			});
			fields = fields.substring(1);
			Ext.Ajax.request({
		   		url : basePath + 'fa/gla/getInternalOffset.action',
		   		params: {
		   			fields: fields,
		   			yearmonth: yearmonth1,
		   			mastercode: mastercode,
		   		},
		   		method : 'post',
		   		callback : function(options,success,response){
		   			me.FormUtil.setLoading(false);
		   			var localJson = new Ext.decode(response.responseText);
		   			if(localJson.exceptionInfo){
		   				showError(localJson.exceptionInfo);
		   			}
	    			if(localJson.success){
	    				form.getForm().setValues(localJson.data);
    					var grid = Ext.getCmp('grid');
    					var param = {caller: grid.caller||caller,condition:'iod_ioid='+localJson.data[form.keyField]};
    					me.GridUtil.loadNewStore(grid, param);
    					yearmonth = yearmonth1;
		   			}
		   		}
			});
		}
	}
});