Ext.define('erp.view.b2b.sale.prodinoutSale',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'prodinoutSaleViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 50%',
					saveUrl: 'b2b/sale/saveprodinoutSale.action',
					deleteUrl: 'b2b/sale/deleteprodinoutSale.action',
					updateUrl: 'b2b/sale/updateprodinoutSale.action',
					auditUrl: 'b2b/sale/auditprodinoutSale.action',
					resAuditUrl: 'b2b/sale/resAuditprodinoutSale.action',
					printUrl: 'b2b/sale/printprodinoutSale.action',
					submitUrl: 'b2b/sale/submitprodinoutSale.action',
					resSubmitUrl: 'b2b/sale/resSubmitprodinoutSale.action',
					endUrl: 'b2b/sale/endprodinoutSale.action',
					resEndUrl: 'b2b/sale/resEndprodinoutSale.action',
					getIdUrl: 'common/getId.action?seq=prodinoutSale_SEQ',
					keyField: 'pi_id',
					codeField: 'pi_code',
					statusField: 'pi_status',
					statuscodeField: 'pi_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 50%', 
					detno: 'pd_detno',
					necessaryField: 'pd_prodcode',
					keyField: 'pd_id',
					mainField: 'pd_puid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});