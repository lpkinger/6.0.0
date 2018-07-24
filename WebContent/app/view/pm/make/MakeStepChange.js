Ext.define('erp.view.pm.make.MakeStepChange',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{ 
				id:'makeStepChangeViewport', 
				layout: 'anchor', 
				items: [{
					xtype: 'erpFormPanel',
					anchor: '100% 30%',
					saveUrl: 'pm/make/saveMakeStepChange.action?caller='+caller ,
					deleteUrl: 'pm/make/deleteMakeStepChange.action?caller='+caller ,
					updateUrl: 'pm/make/updateMakeStepChange.action?caller='+caller ,
					auditUrl: 'pm/make/auditMakeStepChange.action?caller='+caller ,
					submitUrl: 'pm/make/submitMakeStepChange.action?caller='+caller ,
					resSubmitUrl: 'pm/make/resSubmitMakeStepChange.action?caller='+caller ,
					getIdUrl: 'common/getId.action?seq=MakeStepChange_SEQ',
					keyField: 'mc_id',
					codeField: 'mc_code',
					statusField: 'mc_statuscode'
				},{
					xtype: 'erpGridPanel2',
					anchor: '100% 70%', 
					detno: 'md_detno',
					necessaryField: 'md_newstepcode',
					keyField: 'md_id',
					mainField: 'md_mcid'
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});