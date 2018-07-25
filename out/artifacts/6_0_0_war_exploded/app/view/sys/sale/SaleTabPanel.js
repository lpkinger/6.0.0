Ext.define('erp.view.sys.sale.SaleTabPanel',{
	extend: 'Ext.tab.Panel', 
	alias: 'widget.saletabpanel',
	id:'saletabpanel',
	animCollapse: false,
	bodyBorder: false,
	border: false,
	labelSeparator : ':',
	buttonAlign : 'center',
	bodyStyle : 'background:#f9f9f9;',
	fieldDefaults : {
		msgTarget: 'none',
		blankText : $I18N.common.form.blankText
	},
	/*requires:['erp.view.sys.hr.GroupTabPanel','erp.view.sys.hr.DepartPanel','erp.view.sys.hr.JobGrid'],*/
	defaults:{
		xtype:'textfield',
		columnWidth:0.33,
		margin:'5 5 5 5'
	},
	layout:'fit',
	items: [{
		xtype: 'grouptabpanel',
		id:'hrgrouptabpanel',
		activeGroup: 0,
		items: [{
			mainItem: 1,
			activeItem:2,
			items: [/*{
					title: '客户类型',
					iconCls: 'x-icon-subscriptions',
					tabTip: 'Subscriptions tabtip',
					//style: 'padding: 10px;',
					border: false,
					layout: 'border',
					listeners:{
						itemclick:function(grid,record){
							console.log('测试下');
						}
					},
					items:[{
						region:'center',
						xtype:'simpleactiongrid',
						caller:'CustomerKind!Grid',
						saveUrl: 'common/saveCommon.action?caller=CustomerKind',
						deleteUrl: 'common/deleteCommon.action?caller=CustomerKind',
						updateUrl: 'common/updateCommon.action?caller=CustomerKind',
						getIdUrl: 'common/getCommonId.action?caller=CustomerKind',
						keyField:'ck_id',
						params:{
							caller:'CustomerKind!Grid',
							condition:'1=1'
						}	
					},{
						region: 'south',
						//collapsible: true,
						height: 200,
						minHeight: 120,
						condition:"step='Sale'",
						xtype:'formportal',
						split: true,
						caller:'CustomerKind!saas',
						saveUrl: 'common/saveCommon.action?caller=CustomerKind',
						deleteUrl: 'common/deleteCommon.action?caller=CustomerKind',
						updateUrl: 'common/updateCommon.action?caller=CustomerKind',
						getIdUrl: 'common/getCommonId.action?caller=CustomerKind',
						keyField:'ck_id',
						params:{
							caller:'CustomerKind',
							condition:'1=1'
						}	
					}]
			},*/  {
					title: '销售类型',
					iconCls: 'x-icon-subscriptions',
					tabTip: 'Subscriptions tabtip',
					//style: 'padding: 10px;',
					border: false,
					layout: 'border',				
					items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'SaleKind',
					keyField:'sk_id',
					saveUrl: 'scm/sale/saveSaleKind.action?caller=SaleKind',
					deleteUrl: 'scm/sale/deleteSaleKind.action?caller=SaleKind',
					updateUrl: 'scm/sale/updateSaleKind.action?caller=SaleKind',
					getIdUrl: 'common/getId.action?seq=SALEKIND_SEQ',
					keyField: 'sk_id',
					params:{
						caller:'SaleKind!Grid',
						condition:'1=1'
					}
				},{
					region: 'south',
					//collapsible: true,
					height: 200,
					minHeight: 120,
					/*condition:"step='Sale'",*/
					xtype:'formportal',
					split: true,
					caller:'SaleKind!saas',
					keyField:'sk_id',
					saveUrl: 'scm/sale/saveSaleKind.action?caller=SaleKind',
					deleteUrl: 'scm/sale/deleteSaleKind.action?caller=SaleKind',
					updateUrl: 'scm/sale/updateSaleKind.action?caller=SaleKind',
					getIdUrl: 'common/getId.action?seq=SALEKIND_SEQ',
					keyField: 'sk_id',
					params:{
						caller:'SaleKind!saas',
						condition:'1=1'
					}
				}]
			}, {	
				title: '销售模块管理',
				border: false
			},{
				title:'收款方式',
				iconCls: 'x-icon-subscriptions',
				tabTip: 'Subscriptions tabtip',
				//style: 'padding: 10px;',
				border: false,
				layout: 'border',				
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'Payments!Sale',
					saveUrl : 'scm/sale/savePayments.action',
					deleteUrl : 'scm/sale/deletePayments.action',
					updateUrl : 'scm/sale/updatePayments.action',
					getIdUrl: 'common/getId.action?seq=Payments_SEQ',
					keyField : 'pa_id',
					codeField : 'pa_code',
					params:{
						caller:'Payments!Sale!Grid',
						condition:"pa_class='收款方式'"
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'Payments!Sale!saas',
				keyField:'pa_id',
				saveUrl : 'scm/sale/savePayments.action',
				deleteUrl : 'scm/sale/deletePayments.action',
				updateUrl : 'scm/sale/updatePayments.action',
				getIdUrl: 'common/getId.action?seq=Payments_SEQ',
				keyField: 'pa_code',
				params:{
					caller:'Payments!Sale',
					condition:'1=1'
				}
				}]
			},{
				title: '销售预测类型',
				iconCls: 'x-icon-users',
				tabTip: '销售预测类型',
				border: false,
				layout: 'border',				
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'SaleForecastKind',
					saveUrl: 'scm/sale/saveSaleForecastKind.action',
					deleteUrl: 'scm/sale/deleteSaleForecastKind.action',
					updateUrl: 'scm/sale/updateSaleForecastKind.action',
					getIdUrl: 'common/getId.action?seq=SALEFORECASTKIND_SEQ',
					keyField:'sf_id',
					codeField:'sf_code',
					params:{
						caller:'SaleForecastKind!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'SaleForecastKind!saas',
				saveUrl: 'scm/sale/saveSaleForecastKind.action',
				deleteUrl: 'scm/sale/deleteSaleForecastKind.action',
				updateUrl: 'scm/sale/updateSaleForecastKind.action',
				getIdUrl: 'common/getId.action?seq=SALEFORECASTKIND_SEQ',
				keyField:'sf_id',
				codeField:'sf_code',
				params:{
					caller:'SaleForecastKind!saas',
					condition:''
				}
				}]
			},
			{
				title: '借货类型',
				iconCls: 'x-icon-users',
				tabTip: '借货类型',
				border: false,
				layout: 'border',				
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'BorrowCargoType',
					saveUrl: 'common/saveCommon.action?caller=BorrowCargoType',
					deleteUrl: 'common/deleteCommon.action?caller=BorrowCargoType',
					updateUrl: 'common/updateCommon.action?caller=BorrowCargoType',
					getIdUrl: 'common/getCommonId.action?caller=BorrowCargoType',
					keyField:'bt_id',
					codeField:'bt_code',
					defaultValues:[{
						bt_piclass:'借货出货单'
					}],
					params:{
						caller:'BorrowCargoType!Grid',
						condition:"bt_piclass='借货出货单'"
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'BorrowCargoType!saas',
				saveUrl: 'common/saveCommon.action?caller=BorrowCargoType',
				deleteUrl: 'common/deleteCommon.action?caller=BorrowCargoType',
				updateUrl: 'common/updateCommon.action?caller=BorrowCargoType',
				getIdUrl: 'common/getCommonId.action?caller=BorrowCargoType',
				keyField:'bt_id',
				codeField:'bt_code',
				/*defaultValues:[{
					bt_piclass:'借货出货单'
				}],*/
				params:{
					caller:'BorrowCargoType',
					condition:"1=1"
				}
				}]
			}]
		},  {
			expanded: true,		
			items: [{
				title: '采购模块',
				iconCls: 'x-icon-users',
				tabTip: '采购模块',
				border: false,
				layout: 'border',				
				
			}, {
				title: '采购类型',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'PurchaseKind',
					saveUrl: 'common/saveCommon.action?caller=PurchaseKind',
					deleteUrl: 'common/deleteCommon.action?caller=PurchaseKind',
					updateUrl: 'common/updateCommon.action?caller=PurchaseKind',
					getIdUrl: 'common/getCommonId.action?caller=PurchaseKind',
					keyField:'pk_id',
					params:{
						caller:'PurchaseKind!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'PurchaseKind!saas',
				saveUrl: 'common/saveCommon.action?caller=PurchaseKind',
				deleteUrl: 'common/deleteCommon.action?caller=PurchaseKind',
				updateUrl: 'common/updateCommon.action?caller=PurchaseKind',
				getIdUrl: 'common/getCommonId.action?caller=PurchaseKind',
				keyField:'pk_id',
				params:{
					caller:'PurchaseKind',
					condition:'1=1'
					}
				}]
			},{
				title: '付款方式',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'Payments!Purchase',
					saveUrl: 'scm/purchase/savePayments.action',
					deleteUrl: 'scm/purchase/deletePayments.action',
					updateUrl: 'scm/purchase/updatePayments.action',
					getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
					keyField: 'pa_id',
					codeField: 'pa_code',
					statusField: 'pa_auditstatuscode',
					params:{
						caller:'Payments!Purchase!Grid',
						condition:"pa_class='付款方式'"
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'Payments!Purchase!saas',
				saveUrl: 'scm/purchase/savePayments.action',
				deleteUrl: 'scm/purchase/deletePayments.action',
				updateUrl: 'scm/purchase/updatePayments.action',
				getIdUrl: 'common/getId.action?seq=PAYMENTS_SEQ',
				keyField: 'pa_id',
				codeField: 'pa_code',
				statusField: 'pa_auditstatuscode',
				params:{
					caller:'Payments!Purchase',
					condition:'1=1'
				}
				}]
			},{
				title: '其他采购入库类型',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'BorrowCargoType',
					saveUrl: 'common/saveCommon.action?caller=BorrowCargoType',
					deleteUrl: 'common/deleteCommon.action?caller=BorrowCargoType',
					updateUrl: 'common/updateCommon.action?caller=BorrowCargoType',
					getIdUrl: 'common/getCommonId.action?caller=BorrowCargoType',
					keyField:'bt_id',
					codeField:'bt_code',
					defaultValues:[{
						bt_piclass:'其它采购入库单'
					}],
					params:{
						caller:'BorrowCargoType!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
			/*	split: true,*/
				caller:'BorrowCargoType!saas',
				saveUrl: 'common/saveCommon.action?caller=BorrowCargoType',
				deleteUrl: 'common/deleteCommon.action?caller=BorrowCargoType',
				updateUrl: 'common/updateCommon.action?caller=BorrowCargoType',
				getIdUrl: 'common/getCommonId.action?caller=BorrowCargoType',
				keyField:'bt_id',
				codeField:'bt_code',
				/*defaultValues:[{
					bt_piclass:'其它采购入库单'
				}],*/
				params:{
					caller:'BorrowCargoType',
					condition:''
				}
				}]
			}]
		}, {
			expanded: true,		
			items: [{
				title: '物料模块',
				iconCls: 'x-icon-configuration',
				tabTip: '物料模块',
				/*style: 'padding: 10px;',*/
				layout:'border'
			},{
				title: '物料信息',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items :[{
					region:'west',
					xtype:'productkindtree',
					title:'物料种类',
					width:400,
					minWidth:400
				},{ 
					xtype:'tabpanel',
					dockedItems: [Ext.create('erp.view.sys.base.Toolbar')],
					items:[{
						title:'基础设置',
						condition:"step='PR'",
				        itemColumnWidth:1,
						xtype:'modulesetportal',
					},{
			            title: '物料等级',
			            minWidth: 80,
			            xtype:'simpleactiongrid',
			    		caller:'Productlevel',
			    		saveUrl: 'scm/product/saveProductlevel.action',
						deleteUrl: 'scm/product/deleteProductlevel.action',
						updateUrl: 'scm/product/updateProductlevel.action',		
						getIdUrl: 'common/getId.action?seq=Productlevel_SEQ',
			    		keyField:'pl_id',
			    		codeField:'pl_code',
			    		emptyGrid:true,
			    		autoRender:true,
			    		plugins: [{
			    	        ptype: 'cellediting',
			    	        clicksToEdit: 2,
			    	        pluginId: 'cellplugin'
			    	    }],
			    		params:{
			    			caller:'Productlevel!Grid',
			    			condition:'1=1'
			    		}
			        },{
			            title: 'BOM等级',
			            region: 'east',
			            flex: 1,
			            minWidth: 80,
			            xtype:'simpleactiongrid',
			    		caller:'Bomlevel',
			    		saveUrl: 'pm/bom/saveBomlevel.action',
						deleteUrl: 'pm/bom/deleteBomlevel.action',
						updateUrl: 'pm/bom/updateBomlevel.action',
						getIdUrl: 'common/getId.action?seq=Bomlevel_SEQ',
			    		keyField:'bl_id',
			    		autoRender:true,
			    		plugins: [{
			    	        ptype: 'cellediting',
			    	        clicksToEdit: 2,
			    	        pluginId: 'cellplugin'
			    	    }],
			    		params:{
			    			caller:'Bomlevel!Grid',
			    			condition:'1=1'
			    		}
			        }],
					region: 'center',
					minHeight: 120,
					minWidth:200,
					split: true
				},{
					region:'east',
					title:'物料单位',
					xtype:'combosetgrid',
					plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
						clicksToMoveEditor: 1,        
						autoCancel: false
					})],
					animCollapse: true,
					collapsible: true,
					width:235,
					fieldWidth:150,
					margins: '0 5 0 0',
					caller:'Product',
					field:'pr_unit',
					params:{
						caller:'SaleForecastKind!Grid',
						condition:'1=1'
					}
				}/*,{
			        region: 'south',
			       // collapsible: true,
			        split: true,
			        height: 200,
			        minHeight: 120,
			        layout: {
			            type: 'border',
			            padding: 5
			        },
			        defaults:{
			    	    plugins: [{
			    	        ptype: 'cellediting',
			    	        clicksToEdit: 2,
			    	        pluginId: 'cellplugin'
			    	    }]
			    	},
			        items: []
			    }*/]
			}]
		},{
			expanded: true,		
			items: [{
				title: '库存模块',
				iconCls: 'x-icon-configuration',
				tabTip: '库存模块',
				layout:'border'
			}, {
				title: '仓位资料',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'ProductLocation',
					saveUrl: 'common/saveCommon.action?caller=ProductLocation',
					deleteUrl: 'common/deleteCommon.action?caller=ProductLocation',
					updateUrl: 'common/updateCommon.action?caller=ProductLocation',
					getIdUrl: 'common/getId.action?caller=ProductLocation',
					keyField: 'pl_id',
					autoRender:true,
					statusField:'pl_status',
					statusCodeField:'pl_statuscode',
					params:{
						caller:'ProductLocation!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'ProductLocation!saas',
				saveUrl: 'common/saveCommon.action?caller=ProductLocation',
				deleteUrl: 'common/deleteCommon.action?caller=ProductLocation',
				updateUrl: 'common/updateCommon.action?caller=ProductLocation',
				getIdUrl: 'common/getId.action?caller=ProductLocation',
				keyField: 'pl_id',
				autoRender:true,
				statusField:'pl_status',
				statusCodeField:'pl_statuscode',
				params:{
					caller:'ProductLocation',
					condition:'1=1'
				}
				}]
			},{
				title: '仓库资料',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'Warehouse!Base',
					saveUrl: 'common/saveCommon.action?caller=Warehouse!Base',
					deleteUrl: 'common/deleteCommon.action?caller=Warehouse!Base',
					updateUrl: 'common/updateCommon.action?caller=Warehouse!Base',
					getIdUrl: 'common/getCommonId.action?caller=Warehouse!Base',
					keyField:'wh_id',
					autoRender:true,
					params:{
						caller:'Warehouse!Base!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'Warehouse!Base!saas',
				saveUrl: 'common/saveCommon.action?caller=Warehouse!Base',
				deleteUrl: 'common/deleteCommon.action?caller=Warehouse!Base',
				updateUrl: 'common/updateCommon.action?caller=Warehouse!Base',
				getIdUrl: 'common/getCommonId.action?caller=Warehouse!Base',
				keyField:'wh_id',
				autoRender:true,
				params:{
					caller:'Warehouse!Base',
					condition:'1=1'
				}
				}]
			},{
				title:'其它入库类型',
				xtype:'combosetgrid',
				plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToMoveEditor: 1,        
					autoCancel: false
				})],
				animCollapse: true,
				collapsible: true,
				width:235,
				fieldWidth:150,
				margins: '0 5 0 0',
				caller:'ProdInOut!OtherIn',
				field:'pi_type'
			},{

				title:'其它出库类型',
				xtype:'combosetgrid',
				plugins:[Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToMoveEditor: 1,        
					autoCancel: false
				})],
				animCollapse: true,
				collapsible: true,
				width:235,
				fieldWidth:150,
				margins: '0 5 0 0',
				caller:'ProdInOut!OtherOut',
				field:'pi_type'
			}]
		},{
			expanded: true,		
			items: [{
				title: '财务会计模块',
				iconCls: 'x-icon-configuration',
				tabTip: '财务会计模块',
				style: 'padding: 10px;',
				layout:'border'
			}, {
				title: '其它出入库科目',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'ProdIOCateSet',
					saveUrl: 'co/cost/saveProdIOCateSet.action',
					deleteUrl: 'co/cost/deleteProdIOCateSet.action',
					updateUrl: 'co/cost/updateProdIOCateSet.action',
					getIdUrl: 'common/getId.action?seq=PRODIOCATESET_SEQ',
					keyField: 'pc_id',
					codeField: 'pc_code',
					params:{
						caller:'ProdIOCateSet!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'ProdIOCateSet!saas',
				saveUrl: 'co/cost/saveProdIOCateSet.action',
				deleteUrl: 'co/cost/deleteProdIOCateSet.action',
				updateUrl: 'co/cost/updateProdIOCateSet.action',
				getIdUrl: 'common/getId.action?seq=PRODIOCATESET_SEQ',
				keyField: 'pc_id',
				codeField: 'pc_code',
				params:{
					caller:'ProdIOCateSet',
					condition:'1=1'
				}
				}]
			},{
				title: '币别',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'Currencys',
					saveUrl: 'fa/ars/saveCurrencys.action',
					deleteUrl: 'fa/ars/deleteCurrencys.action',
					updateUrl: 'fa/ars/updateCurrencys.action',
					getIdUrl: 'common/getId.action?seq=CURRENCYS_SEQ',
					keyField:'cr_id',
					codeField:'cr_code',
					params:{
						caller:'Currencys!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'Currencys!saas',
				saveUrl: 'fa/ars/saveCurrencys.action',
				deleteUrl: 'fa/ars/deleteCurrencys.action',
				updateUrl: 'fa/ars/updateCurrencys.action',
				getIdUrl: 'common/getId.action?seq=CURRENCYS_SEQ',
				keyField:'cr_id',
				codeField:'cr_code',
				params:{
					caller:'Currencys',
					condition:'1=1'
				}
				}]
			},{
				title: '月度汇率',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'CurrencysMonth',
					keyField:'cm_id',
					deleteUrl: 'fa/fix/CurrencysController/deleteCurrencysMonth.action',
					keyField: 'cm_id',
					params:{
						caller:'CurrencysMonth!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'CurrencysMonth!saas',
				keyField:'cm_id',
				deleteUrl: 'fa/fix/CurrencysController/deleteCurrencysMonth.action',
				keyField: 'cm_id',
				params:{
					caller:'CurrencysMonth',
					condition:'1=1'
				}
				}]
			},{
				title: '固定资产类型',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'AssetsKind',
					saveUrl: 'fa/fix/saveAssetsKind.action',
					deleteUrl: 'fa/fix/deleteAssetsKind.action',
					updateUrl: 'fa/fix/updateAssetsKind.action',
					getIdUrl: 'common/getId.action?seq=ASSETSKIND_SEQ',
					keyField : 'ak_id',
					params:{
						caller:'AssetsKind!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'AssetsKind!saas',
				saveUrl: 'fa/fix/saveAssetsKind.action',
				deleteUrl: 'fa/fix/deleteAssetsKind.action',
				updateUrl: 'fa/fix/updateAssetsKind.action',
				getIdUrl: 'common/getId.action?seq=ASSETSKIND_SEQ',
				keyField : 'ak_id',
				params:{
					caller:'AssetsKind',
					condition:'1=1'
				}
				}]
			},{
				title: '初始化期间设置',
				iconCls: 'x-icon-templates',
				tabTip: 'Templates tabtip',
				border: false,
				layout:'border',
				items: [{
					region:'center',
					xtype:'simpleactiongrid',
					caller:'Periods',
					updateUrl: 'common/updateCommon.action',
					getIdUrl: 'common/getId.action?seq=PERIODS_SEQ',
					keyField : 'pe_id',
					params:{
						caller:'Periods!Grid',
						condition:'1=1'
					}
			},{
				region: 'south',
				//collapsible: true,
				height: 200,
				minHeight: 120,
				/*condition:"step='Sale'",*/
				xtype:'formportal',
				split: true,
				caller:'Periods',
				updateUrl: 'common/updateCommon.action',
				getIdUrl: 'common/getId.action?seq=PERIODS_SEQ',
				keyField : 'pe_id',
				params:{
					caller:'Periods',
					condition:'1=1'
				}
				}]
			}]
		}]
	}],
	initComponent : function(){ 
		this.callParent(arguments);
	}
});

/*Ext.define('erp.view.sys.sale.SaleTabPanel',{
extend: 'Ext.tab.Panel', 
alias: 'widget.saletabpanel',
id:'saletabpanel',
animCollapse: false,
bodyBorder: false,
border: false,
autoShow: true, 
tabPosition:'bottom',
frame:true,
dockedItems: [Ext.create('erp.view.sys.base.Toolbar')],
defaults:{
    plugins: [{
        ptype: 'cellediting',
        clicksToEdit: 2,
        pluginId: 'cellplugin'
    }]
},
items: [{
	title: '客户类型',
	xtype:'simpleactiongrid',
	caller:'CustomerKind',
	saveUrl: 'common/saveCommon.action?caller=CustomerKind',
	deleteUrl: 'common/deleteCommon.action?caller=CustomerKind',
	updateUrl: 'common/updateCommon.action?caller=CustomerKind',
	getIdUrl: 'common/getCommonId.action?caller=CustomerKind',
	keyField:'ck_id',
	params:{
		caller:'CustomerKind!Grid',
		condition:'1=1'
	}
},{
	title: '销售类型',
	xtype:'simpleactiongrid',
	caller:'SaleKind',
	keyField:'sk_id',
	saveUrl: 'scm/sale/saveSaleKind.action?caller=SaleKind',
	deleteUrl: 'scm/sale/deleteSaleKind.action?caller=SaleKind',
	updateUrl: 'scm/sale/updateSaleKind.action?caller=SaleKind',
	getIdUrl: 'common/getId.action?seq=SALEKIND_SEQ',
	keyField: 'sk_id',
	params:{
		caller:'SaleKind!Grid',
		condition:'1=1'
	}
},{
	title:'收款方式',
	xtype:'simpleactiongrid',
	caller:'Payments!Sale',
	saveUrl : 'scm/sale/savePayments.action',
	deleteUrl : 'scm/sale/deletePayments.action',
	updateUrl : 'scm/sale/updatePayments.action',
	getIdUrl: 'common/getId.action?seq=Payments_SEQ',
	keyField : 'pa_id',
	codeField : 'pa_code',
	params:{
		caller:'Payments!Sale!Grid',
		condition:"pa_class='收款方式'"
	}
},{
	title:'销售预测类型',
	xtype:'simpleactiongrid',
	caller:'SaleForecastKind',
	saveUrl: 'scm/sale/saveSaleForecastKind.action',
	deleteUrl: 'scm/sale/deleteSaleForecastKind.action',
	updateUrl: 'scm/sale/updateSaleForecastKind.action',
	getIdUrl: 'common/getId.action?seq=SALEFORECASTKIND_SEQ',
	keyField:'sf_id',
	codeField:'sf_code',
	params:{
		caller:'SaleForecastKind!Grid',
		condition:'1=1'
	}
},{
	title:'借货类型',
	xtype:'simpleactiongrid',
	caller:'BorrowCargoType',
	saveUrl: 'common/saveCommon.action?caller=BorrowCargoType',
	deleteUrl: 'common/deleteCommon.action?caller=BorrowCargoType',
	updateUrl: 'common/updateCommon.action?caller=BorrowCargoType',
	getIdUrl: 'common/getCommonId.action?caller=BorrowCargoType',
	keyField:'bt_id',
	codeField:'bt_code',
	defaultValues:[{
		bt_piclass:'借货出货单'
	}],
	params:{
		caller:'BorrowCargoType!Grid',
		condition:"bt_piclass='借货出货单'"
	}
}],
initComponent : function(){ 
	this.callParent(arguments);
}
});*/