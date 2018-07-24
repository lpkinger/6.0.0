Ext.define('erp.view.scm.reserve.ProdInOut',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpFormPanel',
				anchor: '100% 40%',
				saveUrl: 'scm/reserve/saveProdInOut.action?caller=' +caller,
				deleteUrl: 'scm/reserve/deleteProdInOut.action?caller=' +caller,
				updateUrl: 'scm/reserve/updateProdInOut.action?caller=' +caller,
				auditUrl: 'scm/reserve/auditProdInOut.action?caller=' +caller,
				resAuditUrl: 'scm/reserve/resAuditProdInOut.action?caller=' +caller,
				submitUrl: 'scm/reserve/submitProdInOut.action?caller=' +caller,
				resSubmitUrl: 'scm/reserve/resSubmitProdInOut.action?caller=' +caller,
				postUrl: 'scm/reserve/postProdInOut.action?caller=' +caller,
				printUrl: 'scm/reserve/printProdInOut.action?caller=' +caller,
				printNoCustomerUrl: 'scm/reserve/printProdInOut.action?caller=' +caller,
				printnosaleUrl: 'scm/reserve/printProdInOut.action?caller=' +caller,
				printotherinUrl: 'scm/reserve/printProdInOut.action?caller=' +caller,
				printotheroutUrl: 'scm/reserve/printProdInOut.action?caller=' +caller,
				printBarUrl: 'scm/reserve/printBar.action?caller=' +caller,
				resPostUrl: 'scm/reserve/resPostProdInOut.action?caller=' +caller,
				getIdUrl: 'common/getId.action?seq=PRODINOUT_SEQ',
				keyField: 'pi_id',
				codeField: 'pi_inoutno',
				statusField: 'pi_invostatus',
				statuscodeField: 'pi_invostatuscode',
				statusCode: 'pi_statuscode',//过账状态
				voucherConfig:true
			},{
				xtype: 'erpGridPanel2',
				anchor: '100% 60%', 
				allowExtraButtons: true,
				detno: 'pd_pdno',
				necessaryField: 'pd_prodcode',
				keyField: 'pd_id',
				mainField: 'pd_piid',
				binds: [{
					refFields:['pd_snid'],
					fields:['pd_ordercode','pd_orderdetno']
				},{
					refFields:['pd_qcid'],
					fields:['pd_ordercode','pd_orderdetno', 'pd_prodcode']
				},{
					refFields:['pd_sdid'],
					fields:['pd_ordercode','pd_orderdetno']
				},{
					refFields:['pd_ioid'],
					fields:['pd_ordercode','pd_orderdetno', 'pd_prodcode']
				},{
					refFields:['pd_anid'],
					fields:['pd_ordercode','pd_orderdetno', 'pd_prodcode']
				}]
			}]
		}); 
		me.callParent(arguments); 
	} 
});