Ext.define('erp.view.plm.base.ProjectPhaseView',{    
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpProjectPhaseView',
	id:'erpProjectPhaseView',
	title:'产品阶段计划',
	storeAutoLoad:false,
	columnLines: true,
	bodyStyle : 'background-color:white;',
	keyField:'PH_ID_TEMP',
	deleteUrl:'plm/base/deleteProjectPhase.action',
	BaseUtil:Ext.create('erp.util.BaseUtil'),
	GridUtil:Ext.create('erp.util.GridUtil'),
	viewConfig: {
		stripeRows: true,
		enableTextSelection: true
	},
	store:Ext.create('Ext.data.Store',{
	    storeId:'mistore',
		fields : [ 'PH_ID_TEMP', 'PH_NAME_TEMP', 'PH_REMARK_TEMP','PRJTYPECODE_','PH_DETNO_TEMP'],
	    pageSize: pageSize, 
	    proxy: {
	        type: 'ajax',
	        url: basePath + "plm/base/getProjectPhaseData.action", 
	        extraParams:{
	        	condition:productTypeCode?'PRJTYPECODE_='+productTypeCode:'1=1'
	        },
	        reader: {
	            type: 'json',
	            root: 'data',
	            totalProperty: 'count'
	        }
	    },
	    autoLoad: false
	}),
	initComponent : function(){
		var me=this;
		me.plugins = [me.cellEditingPlugin = Ext.create('Ext.grid.plugin.CellEditing',{
			clicksToEdit:1
		})];
		me.columns=[{
			dataIndex:'PH_ID_TEMP',
			width:0,
			text:'ID'
		},{
			dataIndex:'PH_DETNO_TEMP',
			width:60,
			header:'序号',
			align:'center',
			editor : {
				xtype:'numberfield',
				decimalPrecision:0
			},
			minValue:1,
			hidden : false,
			renderer:function(val,meta,record){
				if(val&&val.toString().indexOf('.')>0){
					return val.substring(0,val.indexOf('.'));
				}else{
					return val;
				}
			}
		},{
			dataIndex:'PRJTYPECODE_',
			width:100,
			align:'center',
			text:'产品编号',
			hidden:true
		},{
			dataIndex:'PH_NAME_TEMP',
			width:300,
			text:'阶段描述',
			align:'left',
			style:'text-align:center',
			editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:false,
				maxLength:50
			}
		},{
			dataIndex:'PH_REMARK_TEMP',
			flex:1,
			align:'left',
			text:'备注',
			style:'text-align:center',
			editor: {
				xtype: 'textfield',
				selectOnFocus: true,
				allowOnlyWhitespace: false,
				allowBlank:true,
				maxLength:500
			},
		}];
		me.dockedItems =[{
			xtype:'toolbar',
			dock:'top',
			items:[{
				xtype:'button',
				text:'新增',
				iconCls:'x-button-icon-add',
				id:'add',
				cls:'x-btn-gray',
				width:60,
				style:'margin-left:5px;margin-top:10px'
			},{
				xtype:'button',
				text:'保存',
				iconCls: 'x-button-icon-save',
				cls:'x-btn-gray',
				width:60,
				margin:'1 2 1 2',
				style: {
		    		marginLeft: '10px'
		        },
		        handler:function(btn){
		        	var grid = Ext.getCmp("erpProjectPhaseView");
		        	
		        	var datas = grid.store.data.items;
					//空的序号赋值
		        	var max = me.getMaxDetno(datas);
					var count = 1;
					Ext.Array.each(grid.store.data.items,function(item){
						if(item.data.PH_DETNO_TEMP == null || item.data.PH_DETNO_TEMP == ""
								|| typeof(item.data.PH_DETNO_TEMP) == 'undefined'){
							item.set('PH_DETNO_TEMP',max + count);
							count++;
						}					
					});

					
					var store = me.GridUtil.getGridStore(grid);
					if(!store[0]){
						showError("请输入要保存的数据！");
						return;
					}
					Ext.Ajax.request({
						url : basePath + 'plm/base/saveProjectPhase.action',
						params:{
							productTypeCode:productTypeCode,
							gridStore:'['+store+']'
							
						},
						method:'post',
						callback:function(options,success,response){
							var res = new Ext.decode(response.responseText);
							if(res.success){
								Ext.Msg.alert('提示','保存成功！');
								Ext.getCmp("erpProjectPhaseView").getStore().load();
							} else if(res.exceptionInfo){
								var str = res.exceptionInfo;
								Ext.Msg.alert('保存失败',str);
							}
						}
					});
		        }
			}/*,{
				xtype:'button',
				text:'更新',
				iconCls:'x-button-icon-add',
				id:'update',
				cls:'x-btn-gray',
				width:60,
				style:'margin-left:5px;margin-top:10px'
			}*/,{
				xtype:'button',
				text:'删除',
				iconCls:'x-button-icon-delete',
				id:'delete',
				disabled:true,
				cls:'x-btn-gray',
				width:60,
				style:'margin-left:5px;margin-top:10px'
			},{
				xtype:'button',
				text:'下载模板',
				iconCls: 'x-button-icon-download',
				cls:'x-btn-gray',
				width:85,
				margin:'1 2 1 2',
				style: {
		    		marginLeft: '10px'
		        },
				handler:function(btn){
					var grid = Ext.getCmp("erpProjectPhaseView");
					var store = grid.getView().getStore();
					store.loadData([{}]);
					grid.BaseUtil.exportGrid(grid,"产品阶段计划");
					grid.store.load();
				}
			},{
				xtype:'upexcel',
				text:'导入数据',
				iconCls : 'x-button-icon-up',
				cls:'x-btn-gray',
				height:26,
				width:85,
				margin:'1 2 1 2',
				listeners:{
					afterrender:function(form){						
						var fileField = Ext.getCmp('erpProjectPhaseView').query('filefield')[0];
						fileField.on('change',function(self){
							Ext.defer(function(){
								var win = Ext.getCmp('excelwin');
								var confirmBtn = Ext.getCmp('confirmimport');
								var phgrid = Ext.getCmp('erpProjectPhaseView');
								if(confirmBtn){
									confirmBtn.handler = function(){
										var tgrid = Ext.getCmp('excelgrid');
										var radioValue = win.down('radiogroup').getValue();
										if(radioValue.import_mode=='-'){ //替换模式
											if(phgrid.store.data.items.length>0){
												Ext.Msg.confirm('确认','该类型下已存在数据，需先清空，是否清空?',function(btn){
													if(btn=='yes'){
														//替换模式先删除原有数据
														var param = [{
															PH_ID_TEMP:"(select ph_id_temp from projectphase_temp where prjtypecode_='"+productTypeCode+"')"
														}];
														Ext.Ajax.request({
															url : basePath + phgrid.deleteUrl,
															async:false,
															params : {
																id : Ext.encode(param)
															},
															callback : function(options, success, response) {
																var res = Ext.decode(response.responseText);
																if (res.exceptionInfo){
																	showError(res.exceptionInfo);
																}
															}
														});																										
														phgrid.exportToGrid(fileField,tgrid,win);
														return;
													}else{
														return false;
													}
												});											
											}else{
												phgrid.exportToGrid(fileField,tgrid,win);
											}
										}else{
											fileField.ownerCt.exportGridToGrid(tgrid, phgrid, function(btn){
												win.close();
											});											
										}
										
									};
								}
							},600);						
						});
					}
				}
			},'->']
		},{
			xtype:'pagingtoolbar',
			dock:'bottom',
			displayInfo: true,
			store:Ext.data.StoreManager.lookup('mistore')
		}],
		this.callParent(arguments);
	},
	exportToGrid:function(fileField,tgrid,win){
		fileField.ownerCt.exportGridToGrid(tgrid, this, function(btn){
			win.close();
		});
		
		Ext.Array.each(this.store.data.items,function(item,index){
			item.data.PH_ID_TEMP = "";
		});		
	},
	getMaxDetno:function(data){
		var max = 0;
		Ext.Array.each(data,function(d){
			if(d.data['PH_DETNO_TEMP']&& parseInt(d.data['PH_DETNO_TEMP'])>max){
				max =  parseInt(d.data['PH_DETNO_TEMP']);
			}
		});
		return max;
	},
});