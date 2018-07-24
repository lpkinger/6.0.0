Ext.define('erp.view.drp.aftersale.reservelin',{
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 35%',
					saveUrl: 'drp/aftersale/saveReservelin.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteReservelin.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateReservelin.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditReservelin.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditReservelin.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitReservelin.action?caller=' +caller,
					printUrl: 'drp/aftersale/printReservelin.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitReservelin.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PRODINOUT_SEQ',
					keyField: 'pi_id',
					codeField: 'pi_inoutno',
					statusField: 'pi_invostatus',
					statuscodeField: 'pi_invostatuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 65%',
                    necessaryField: '',
					keyField: 'pd_id',
					detno: 'pd_pdno',
					mainField: 'pd_piid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});