Ext.define('erp.view.b2b.reserve.ProdIODown',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',			
				getIdUrl: 'common/getId.action?seq=PRODIODOWN_SEQ',
				keyField: 'pi_id',
				codeField: 'pi_inoutno'
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				allowExtraButtons: true,
				detno: 'pd_detno',				
				keyField: 'pd_id',
				mainField: 'pd_piid',
				readOnly:true,
				bbar: {xtype: 'erpToolbar',id:'toolbar', enableAdd: false,enableDelete: false}
			}]
		}); 
		me.callParent(arguments); 
	} 
});