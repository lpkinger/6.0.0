Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.WarehouseMan', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','scm.reserve.WarehouseMan','core.grid.Panel2','core.toolbar.Toolbar',
     		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update',
     			'core.button.Add','core.button.DeleteDetail','core.button.Scan','core.button.ClearWareMan',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger','core.trigger.MultiDbfindTrigger'
     	],
   init:function(){
	   var me = this;
	   	me.allowinsert = true;
		this.control({
		   'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpAddButton': {
   			click: function(){
   				me.FormUtil.onAdd('addWarehouseMan', '新仓库仓管员', 'jsps/scm/reserve/warehouseMan.jsp');
   			}
   		},
			'erpSaveButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},
			'erpUpdateButton': {
				click: function(btn){
					this.FormUtil.onUpdate(this);
				}
			},    		
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'field[name=wh_id]' : {
				change : function(f) {
					if (f.value != null && f.value != '') {
						window.location.href = window.location.href
								.toString().split('?')[0]
								+ '?formCondition=wh_id='
								+ f.value
								+ '&gridCondition=wm_whid=' + f.value;
					} else {
						Ext.getCmp('deletebutton').hide();
						Ext.getCmp('updatebutton').hide();
					}
				}
			}
		});
	}, 
	onGridItemClick: function(selModel, record){//grid行选择
		this.GridUtil.onGridItemClick(selModel, record);
	},
	getForm: function(btn){
		return btn.ownerCt.ownerCt;
	}
});