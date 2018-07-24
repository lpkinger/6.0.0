Ext.define('erp.view.scm.reserve.CancelProdInOut',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				xtype: 'erpCancelFormPanel',
				anchor: '100% 40%',
				keyField: 'pi_id',
				codeField: 'pi_inoutno',
				statusField: 'pi_invostatus',
				statuscodeField: 'pi_invostatuscode',
				statusCode: 'pi_statuscode',//过账状态
				voucherConfig:true
			},{
				xtype: 'erpCancelGridPanel',
				anchor: '100% 60%', 
				allowExtraButtons: true,
				detno: 'pd_pdno',
				necessaryField: 'pd_prodcode',
				keyField: 'pd_id',
				mainField: 'pd_piid',
				binds: [{
					refFields:['pd_snid'],
					fields:['pd_ordercode','pd_orderdetno', 'pd_prodcode']
				},{
					refFields:['pd_qcid'],
					fields:['pd_ordercode','pd_orderdetno', 'pd_prodcode']
				},{
					refFields:['pd_sdid'],
					fields:['pd_ordercode','pd_orderdetno', 'pd_prodcode']
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