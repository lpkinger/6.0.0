

Ext.define('erp.view.WisdomPark.Agreement', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		Ext.apply(this, {
			items : [{
				xtype: 'erpFormPanel',
				saveUrl: 'wisdomPark/saveAgreement.action',
				deleteUrl: 'wisdomPark/deleteAgreement.action',
				updateUrl: 'wisdomPark/updateAgreement.action',
				publishUrl: 'wisdomPark/publishAgreement.action',
				cancelUrl: 'wisdomPark/cancelAgreement.action',
				getIdUrl: 'common/getId.action?seq=AGREEMENT_SEQ',
				keyField:'ag_id',
				getValues: function(asString, dirtyOnly){
					var values = {};
					
			        Ext.Array.each(this.items.items, function(field){
			        	var val = '';
			        	if(field.name!='ag_content'){
			        		if(field.xtype=='datefield'){
			        			if(field.value)
			        				val = Ext.Date.format(field.value,field.format);
			        		}else if(typeof(field.getValue)=='function'){
			        			val = field.getValue();
			        		}else {
			        			val = field.value;
			        		}
			        	}else{
			        		val = field.getValue();
			        	}
			        	values[field.name] = val;
			        });
						
			        return values;
				}
			}]
		});

		this.callParent(arguments);
	}
});