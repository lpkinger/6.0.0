Ext.QuickTips.init();
Ext.define('erp.controller.scm.reserve.SplitProdIODetail', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
      		'core.form.Panel','scm.reserve.SplitProdIODetail','core.grid.Panel2','core.toolbar.Toolbar',
      		'core.button.Save','core.button.Close','core.button.DeleteDetail',
  			'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
  	],
	init:function(){
		var me = this;
		this.control({
			'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},
			'erpSaveButton':{
				click: function(btn){
					var grid = Ext.getCmp('grid'), items = grid.store.data.items;
					var bool = true;
					var inqty = 0, outqty = 0, 
						qty = Number(Ext.getCmp('pd_inqty').value) + Number(Ext.getCmp('pd_outqty').value);
					Ext.Array.each(items, function(item) {
						 if (!Ext.isEmpty(item.data['pd_inqty']) || !Ext.isEmpty(item.data['pd_outqty'])){
							 if(Number(item.data['pd_inqty']) + Number(item.data['pd_outqty']) < Number(item.data['pd_yqty'])){
								 bool = false;
								 showError('分拆数量不能小于已转数量!') ;  
			    				 return;
							 }
							 inqty= inqty + Number(item.data['pd_inqty']);
							 outqty= outqty + Number(item.data['pd_outqty']);
						 }
					});
					if(qty!=inqty + outqty){
						bool = false;
						showError('分拆数量必须等于原数量!') ;  
    					return;
					}
					if(bool){
						this.FormUtil.beforeSave(this);
					}
				}
    		},
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'erpDeleteDetailButton': {
				afterrender:function(btn){
					btn.handler=function(){
						var sm = Ext.getCmp('grid').getSelectionModel();
		                var record=sm.getSelection(), pd_id=record[0].data.pd_id;
		                if(pd_id && pd_id != 0){
		                	Ext.Msg.alert('提示','不能删除已拆批次或原始行号!');
		                	return;
		                }
		                var store=Ext.getCmp('grid').getStore();
		                store.remove(record);
		                if (store.getCount() > 0) {
		                    sm.select(0);
		                }
					};
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