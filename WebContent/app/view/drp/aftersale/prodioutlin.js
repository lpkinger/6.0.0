Ext.define('erp.view.drp.aftersale.prodioutlin',{
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
					saveUrl: 'drp/aftersale/saveProdioutlin.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteProdioutlin.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateProdioutlin.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditProdioutlin.action?caller=' +caller,
					printUrl: 'scm/reserve/printProdInOut.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditProdioutlin.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitProdioutlin.action?caller=' +caller,
					//printUrl: 'drp/aftersale/printProdioutlin.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitProdioutlin.action?caller=' +caller,
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