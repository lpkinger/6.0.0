Ext.QuickTips.init();
Ext.define('erp.controller.ma.RelativeSearch', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    views:[
    		'ma.RelativeSearch','core.form.Panel','core.grid.Panel2','core.toolbar.Toolbar',
    		'ma.RelativeSearchGrid','core.button.Add','core.button.Save','core.button.Close',
    		'core.button.Update','core.button.Delete','core.form.YnField',
    		'core.button.DeleteDetail','core.trigger.DbfindTrigger','core.grid.YnColumn',
    		'core.form.YnField', 'core.trigger.MultiDbfindTrigger','core.trigger.DbfindTrigger'
    	],
    init:function(){
    	var me = this;
    	this.control({ 
    		'erpGridPanel2': { 
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record);
    			},
    			reconfigure: function(grid) {
    				Ext.defer(function(){
    					grid.readOnly = false;
    				}, 200);
    			}
    		},
    		'relativesearchgrid':{
    			itemclick: function(selModel, record){
    				this.GridUtil.onGridItemClick(selModel, record, 'relativesearchgrid');
    			},
    			reconfigure: function(grid) {
    				Ext.defer(function(){
    					grid.readOnly = false;
    				}, 200);
    			}
    		},
    		'field[name=rsf_field]': {
    			afterrender:function(t){
    				t.gridKey="rs_caller";
    				t.mappinggirdKey="dg_caller";
    				t.gridErrorMessage="请先填写CALLER";
    			}
    		},
    		'erpSaveButton': {
    			click: function(btn){
    				this.beforeSave();
    			}
    		},
    		'erpCloseButton': {
    			click: function(btn){
    				this.FormUtil.beforeClose(this);
    			}
    		},
    		'erpUpdateButton': {
    			click: function(btn){
    				this.beforeUpdate();
    			}
    		},
    		'erpDeleteButton': {
    			click: function(btn){
    				showError('关联查询配置不允许删除！请联系售后！');
					return;
    			}
    		},
    		'erpAddButton': {
    			click: function(){
    				me.FormUtil.onAdd('addRelativeSearch', '新增关联查询', 'jsps/ma/relativeSearch.jsp');
    			}
    		}
    	});
    },
    getForm: function(btn){
		return btn.ownerCt.ownerCt;
	},
	beforeSave: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid'), relativesearchgrid = Ext.getCmp('relativesearchgrid');
		var param1 = me.GridUtil.getGridStore(detail), param2 = me.GridUtil.getGridStore(relativesearchgrid);
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				var r = form.getValues();
				me.FormUtil.save(r, param1, param2);
			}else{
				me.FormUtil.checkForm();
			}
	},
	beforeUpdate: function(){
		var me = this;
		var form = Ext.getCmp('form');
		if(! me.FormUtil.checkForm()){
			return;
		}
		if(Ext.getCmp(form.keyField).value == null || Ext.getCmp(form.keyField).value == ''){
			me.FormUtil.getSeqId(form);
		}
		var detail = Ext.getCmp('grid');
		var relativesearchgrid = Ext.getCmp('relativesearchgrid');
		var param1 = me.GridUtil.getGridStore(detail);
		var param2 = me.GridUtil.getGridStore(relativesearchgrid);
			param1 = param1 == null ? [] : "[" + param1.toString().replace(/\\/g,"%") + "]";
			param2 = param2 == null ? [] : "[" + param2.toString().replace(/\\/g,"%") + "]";
			if(form.getForm().isValid()){
				var r = form.getValues();
				me.FormUtil.update(r, param1, param2);
			}else{
				me.FormUtil.checkForm();
			}
	}
});