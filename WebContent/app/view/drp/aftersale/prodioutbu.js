Ext.define('erp.view.drp.aftersale.prodioutbu',{
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
					saveUrl: 'drp/aftersale/saveProdioutbu.action?caller=' +caller,
					deleteUrl: 'drp/aftersale/deleteProdioutbu.action?caller=' +caller,
					updateUrl: 'drp/aftersale/updateProdioutbu.action?caller=' +caller,
					auditUrl: 'drp/aftersale/auditProdioutbu.action?caller=' +caller,
					resAuditUrl: 'drp/aftersale/resAuditProdioutbu.action?caller=' +caller,
					submitUrl: 'drp/aftersale/submitProdioutbu.action?caller=' +caller,
					printUrl: 'drp/aftersale/printProdioutbu.action?caller=' +caller,
					resSubmitUrl: 'drp/aftersale/resSubmitProdioutbu.action?caller=' +caller,
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