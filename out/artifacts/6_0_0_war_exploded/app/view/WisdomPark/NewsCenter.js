

Ext.define('erp.view.WisdomPark.NewsCenter', {
	extend : 'Ext.Viewport',
	layout : 'fit',
	hideBorders : true,
	initComponent : function() {
		Ext.apply(this, {
			items : [{
				xtype: 'erpFormPanel',
				saveUrl: 'wisdomPark/newsCenter/saveNews.action',
				deleteUrl: 'wisdomPark/newsCenter/deleteNews.action',
				updateUrl: 'wisdomPark/newsCenter/updateNews.action',
				publishUrl: 'wisdomPark/newsCenter/publishNews.action',
				cancelUrl: 'wisdomPark/newsCenter/cancelNews.action',
				getIdUrl: 'common/getId.action?seq=NEWSCENTER_SEQ',
				keyField:'nc_id',
				getValues: function(asString, dirtyOnly){
					var values = {};
					
			        Ext.Array.each(this.items.items, function(field){
			        	var val = '';
			        	if(field.name!='nc_content'){
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