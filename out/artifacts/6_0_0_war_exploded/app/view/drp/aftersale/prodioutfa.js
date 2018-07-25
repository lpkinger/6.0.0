Ext.define('erp.view.drp.aftersale.prodioutfa',{
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
					saveUrl: 'drp/aftersale/saveProdioutfa.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteProdioutfa.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateProdioutfa.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditProdioutfa.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditProdioutfa.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitProdioutfa.action?caller=' +caller,
					printUrl: 'drp/aftersale/printProdioutfa.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitProdioutfa.action?caller=' +caller,
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