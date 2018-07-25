Ext.define('erp.view.drp.aftersale.prodiouttui',{
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
					saveUrl: 'drp/aftersale/saveProdiouttui.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteProdiouttui.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateProdiouttui.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditProdiouttui.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditProdiouttui.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitProdiouttui.action?caller=' +caller,
					//printUrl: 'drp/aftersale/printProdiouttui.action?caller=' +caller,
					printUrl: 'scm/reserve/printProdInOut.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitProdiouttui.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PRODINOUT_SEQ',
					keyField: 'pi_id',
					codeField: 'pi_inoutno',
					statusField: 'pi_status',
					statuscodeField: 'pi_statuscode'
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