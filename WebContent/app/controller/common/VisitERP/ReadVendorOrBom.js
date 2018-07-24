Ext.QuickTips.init();
Ext.define('erp.controller.common.VisitERP.ReadVendorOrBom', {
    extend: 'Ext.app.Controller',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
    BaseUtil: Ext.create('erp.util.BaseUtil'),
    views:[
     		'core.form.Panel','common.VisitERP.VendorBomRead','common.VisitERP.BomVendorRead','core.grid.Panel2','core.toolbar.Toolbar', 'core.form.MultiField', 
     		'core.button.Save','core.button.Upload','core.button.Close','core.button.Update',
     			'core.button.Add','core.button.DeleteDetail',
      		'core.trigger.DbfindTrigger','core.trigger.TextAreaTrigger'
     	],
   init:function(){
	   	var me = this;
	   	me.allowinsert = true;
		this.control({
			'erpFormPanel': { 
			},
		    'erpGridPanel2': { 
				itemclick: this.onGridItemClick
			},	
			'erpCloseButton': {
				click: function(btn){
					me.FormUtil.beforeClose(me);
				}
			},
			'dbfindtrigger[name=ve_code]':{
    			afterrender: function(t){
    				t.setEditable(false);
					t.dbBaseCondition = "ve_uu is not null";
    			},
    			aftertrigger: function(btn){
    				var id = Ext.getCmp('ve_id').value;
    				var form=Ext.getCmp('form');
    				var grid =Ext.getCmp('grid');
    				if(id != null & id != ''){
    					var formCondition = form.keyField + "IS" + id ;
						var gridCondition = grid.mainField + "IS" + id;;
	    				window.location.href = basePath+'jsps/common/VisitERP/VendorBomRead.jsp'+ '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
    				}
    			}
    		},
    		'dbfindtrigger[name=bo_id]':{
    			aftertrigger: function(btn){
    				var id = Ext.getCmp('bo_id').value;
    				var form=Ext.getCmp('form');
    				var grid =Ext.getCmp('grid');
    				if(id != null & id != ''){
    					var formCondition = form.keyField + "IS" + id ;
						var gridCondition = grid.mainField + "IS" + id;;
	    				window.location.href = basePath+'jsps/common/VisitERP/BomVendorRead.jsp'+ '?formCondition=' + 
								formCondition + '&gridCondition=' + gridCondition;
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