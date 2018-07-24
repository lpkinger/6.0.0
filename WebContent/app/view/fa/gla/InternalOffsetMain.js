Ext.define('erp.view.fa.gla.InternalOffsetMain',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	initComponent : function(){ 
		Ext.apply(this, { 
			items: [{
				xtype: 'erpFormPanel',
				width: '100%',
				region: 'north',
				layout:'column',
	    		frame:true,
	    		defaults:{
	    			margin: '5 10 8 5',
	    			xtype:'textfield',
	    			labelWidth: 80,
	    			columnWidth:0.25
	    		},
	    		getItemsAndButtons: function(){
	    			Ext.apply(this,{
		    			items:[{
			    			xtype: 'monthdatefield',
			    			fieldLabel: '期间'	,
			    			id: 'yearmonth',
							name: 'yearmonth',
			    			allowBlank: false,
					    	readOnly: false
			    		},{
			    			fieldLabel:'本位币',
			    			id:'currency',
			    			name: 'currency',
			    			allowBlank: false,
					    	readOnly: false
			    		}]
	    			});
	    		},
	    		bbar:['->',{
					xtype: 'erpCatchDataButton'
				},{
					xtype: 'button',
					text: '导出',
					iconCls : 'x-button-icon-excel',
					cls : 'x-btn-gray',
					handler: function(btn){
						var yearmonth = Ext.getCmp('yearmonth').value;
						Ext.Ajax.request({
							url: basePath + 'fa/gla/InternalOffsetValid.action',
							params: {
								yearmonth: yearmonth
							},
							success: function(response){
								var data = response.responseText;
								if(data == 'true'){
									window.location.href=basePath+'fa/gla/InternalOffsetExportExcel.action?yearmonth='+yearmonth+"&_noc=1";
								}
							}
						});
					}
				},{
					xtype: 'erpCloseButton'
				},'->']
			},{
				xtype:'tabpanel',
				region: 'center',
				plugins:[new Ext.ux.TabScrollerMenu({
					pageSize: 10,
		            maxText  : 15
		        })]
			}]
		}); 
		this.callParent(arguments); 
	}
});