Ext.define('erp.view.drp.distribution.ProdInOut',{ 
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
					anchor: '100% 40%',
					saveUrl: 'drp/distribution/saveProdInOut.action?caller=' +caller,
					deleteUrl: 'drp/distribution/deleteProdInOut.action?caller=' +caller,
					updateUrl: 'drp/distribution/updateProdInOut.action?caller=' +caller,
					auditUrl: 'drp/distribution/auditProdInOut.action?caller=' +caller,
					resAuditUrl: 'drp/distribution/resAuditProdInOut.action?caller=' +caller,
					submitUrl: 'drp/distribution/submitProdInOut.action?caller=' +caller,
					resSubmitUrl: 'drp/distribution/resSubmitProdInOut.action?caller=' +caller,
					postUrl: 'drp/distribution/postProdInOut.action?caller=' +caller,
					printUrl: 'drp/distribution/printProdInOut.action?caller=' +caller,
					resPostUrl: 'drp/distribution/resPostProdInOut.action?caller=' +caller,
					getIdUrl: 'common/getId.action?seq=PRODINOUT_SEQ',
					keyField: 'pi_id',
					codeField: 'pi_inoutno',
					statusField: 'pi_invostatus',
					statuscodeField: 'pi_invostatuscode',
					statusCode: 'pi_statuscode'//过账状态
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 60%', 
					detno: 'pd_pdno',
					necessaryField: 'pd_prodcode',
					keyField: 'pd_id',
					mainField: 'pd_piid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});