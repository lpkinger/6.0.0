Ext.define('erp.view.fa.gla.ConsolidatedMain',{ 
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
			    			xtype: 'combo',
			    			fieldLabel: '报表类型',
			    			id: 'fatype',
							name: 'fatype',
							store:Ext.create('Ext.data.Store', {
							    fields: ['display', 'value'],
							    data : [
							        {display:'资产负债表', value:'资产负债表'},
							        {display:'利润表', value:'利润表'}
							    ]
							}),
							queryMode: 'local',
		    				displayField: 'display',
		    				valueField: 'value',
							value:'资产负债表'
			    		},{
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
						var yearmonth = Ext.getCmp('yearmonth').value,
							fatype = Ext.getCmp('fatype').value,
							kind = '集团报表';
						Ext.Ajax.request({
							url: basePath + 'fa/gla/childReportValid.action',
							params: {
								yearmonth: yearmonth,
								fatype: fatype,
								kind: kind
							},
							success: function(response){
								var data = response.responseText;
								if(data == 'true'){
									window.location.href=basePath+'fa/gla/exportMultitabExcel.action?yearmonth='+yearmonth+"&fatype="+fatype+"&kind="+kind+"&_noc=1";
								}
							}
						});
					}
				}
//	    		,{
//					xtype:'erpReportAccountButton'
//				}
	    		,{
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