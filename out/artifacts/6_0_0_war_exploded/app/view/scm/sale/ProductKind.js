Ext.define('erp.view.scm.sale.ProductKind',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				region: 'center',
				width: '62%',
				saveUrl: 'scm/sale/saveProductKind.action',
				deleteUrl: 'scm/sale/deleteProductKind.action',
				updateUrl: 'scm/sale/updateProductKind.action',
				auditUrl : 'scm/sale/auditProductKind.action',
				resAuditUrl : 'scm/sale/resAuditProductKind.action',
				submitUrl : 'scm/sale/submitProductKind.action',
				resSubmitUrl : 'scm/sale/resSubmitProductKind.action',
				getIdUrl: 'common/getId.action?seq=PRODUCTKIND_SEQ',
				keyField: 'pk_id',
			    codeField: 'pk_code',
			    refresh: function(form) {
			    	var id = form.down('#pk_id').getValue();
			    	if(id) {
			    		form.FormUtil.loadNewStore(form, {
				    		caller: caller, 
				    		condition: "pk_id=" + form.down('#pk_id').getValue()
				    	});
			    	}
			    }
			},{
				region: 'west',
				width: 220,
				isAnother:true,
				xtype: 'prodkindtree',
				allKind:true,				
				tbar: [{
					iconCls: 'tree-add',
					name: 'add',
					text: $I18N.common.button.erpAddButton,
					hidden: true
				},{
					iconCls: 'tree-delete',
					name: 'delete',
					text: $I18N.common.button.erpDeleteButton,
					hidden: true
				},{
					width: 132,
			        xtype: 'searchfield',
			        caller:'ProductKind',
			        id: 'ProductKindSearch',
			        style: 'margin-left:15px;'
				},{
			        iconCls: 'tree-back',
			        cls: 'x-btn-tb',
			        width: 16,
			        style: 'margin-left:20px;',
			        tooltip: $I18N.common.main.treeBack,
			        hidden: false,
			    	handler: function(){
			    		Ext.getCmp('tree-panel').getTreeRootNode(0);
			    		Ext.getCmp('tree-panel').down('erpTreeToolbar').down('searchfield').setValue(null);
			    	}
			    }]
			}]
		}); 
		me.callParent(arguments); 
	} 
});