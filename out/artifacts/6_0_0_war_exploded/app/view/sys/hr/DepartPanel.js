Ext.define('erp.view.sys.hr.DepartPanel',{    
	extend: 'Ext.panel.Panel', 
	layout:'border',
	alias: 'widget.departpanel',
	frame:false,
	initComponent : function(){
		var me=this;
		Ext.applyIf(me,{
			items:me.configItem(me)
		});	
		this.callParent(arguments);
	},
	configItem:function(panel){
		return [panel.configDepartGrid(),panel.configHelpText()];
	},
	configDepartGrid:function(){
		var config={
				region:'center',
				id:'departgrid',
				caller:'Department',
				columnLines:true,
				keyField:'dp_id',
				saveUrl:'hr/employee/saveDepartment.action',
				updateUrl:'hr/employee/updateDepartment.action',
				deleteUrl:'hr/employee/deleteDepartment.action',
				getIdUrl: 'common/getId.action?seq=Department_SEQ',
				viewConfig: {
					stripeRows: true,
					enableTextSelection: true
				},
				dockedItems: [{
					xtype: 'toolbar',
					ui: 'footer',
					items: [{
						text:'添加',
						itemId: 'adddepart',
						tooltip:'添加新记录',
						iconCls:'btn-add'
					},'-',{
						text:'载入标准数据',
						itemId:'loadStandardData',
						table:'Department',
						iconCls:'btn-get',
						tooltip:'从标准帐套重新载入'
					},'-', {
						text:'帮助',
						iconCls:'btn-help',
						tooltip:'帮助简介'
					}]
				}],
				columns:[{
					dataIndex:'dp_id',
					width:0,
					text:'ID'
				},Ext.create('Ext.grid.RowNumberer',{
					width:35
				}),{
					dataIndex:'dp_code',
					width:100,
					text:'部门编号'
				},{
					dataIndex:'dp_name',
					width:120,
					text:'部门名称'
				},{
					xtype:'actioncolumn',
					width:45,
					text :'操作',
					items:[{
						iconCls:'btn-edit',
						tooltip:'修改',
						handler:function(grid, rowIndex, colIndex,item) {
							var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt,deparpanel=gridpanel.ownerCt;
							var win=Ext.widget('detailwindow',{
								title:gridpanel.title,
								items:[Ext.widget('formportal',{
									region:'center', 
									caller:gridpanel.caller,
									updateUrl: gridpanel.updateUrl,
									saveSuccess:deparpanel.DetailUpdateSuccess,
									condition:gridpanel.keyField+'='+record.get(gridpanel.keyField)
								})]
							});
							win.showRelyBtn(win,grid);
						}
					},
					{
						iconCls:'btn-delete',
						tooltip:'删除',
						width:75,
						handler:function(grid, rowIndex, colIndex) {
							Ext.Msg.confirm('删除数据?', '确定要删除当前选中行(行号:'+(rowIndex+1)+')?',
									function(choice) {
								if(choice === 'yes') {
									//var reviewStore = Ext.data.StoreMgr.lookup('reviewStore');
									var record = grid.getStore().getAt(rowIndex),gridpanel=grid.ownerCt;
									gridpanel.setLoading(true);
										Ext.Ajax.request({
											url : basePath +'hr/employee/deleteDepartment.action',
											params: {
												id: record.get('dp_id')
											},
											method : 'post',
											callback : function(options,success,response){
												gridpanel.setLoading(false);
												var localJson = new Ext.decode(response.responseText);
												if(localJson.exceptionInfo){
													showError(localJson.exceptionInfo);return;
												}
												if(localJson.success){
													showResult('提示','删除成功!');
													grid.getStore().load();
												} else {
													delFailure();
												}
											}
										});
									}  
						})
					}
					}]
				}],
				store:Ext.create('Ext.data.Store',{
					fields:[{name:'dp_id',type:'number'},
					        {name:'dp_code',type:'string'},
					        {name:'dp_name',type:'string'}],
					        proxy: {
					        	type: 'ajax',
					        	url: basePath+'/hr/employee/getDepartments.action',
					        	reader: {
					        		type: 'json',
					        		root: 'departs'
					        	}
					        }, 
					        autoLoad: true       
				})
		};
		return   Ext.widget('gridpanel',config);
	},
	configHelpText:function(){
		return {
			xtype:'panel',
			region:'east',
			width:'40%',
			html:'帮助信息'
		};
	},
	removeDetail:function(grid,id){
		grid.setLoading(true);
		Ext.Ajax.request({
			url : basePath + grid.deleteUrl,
			params: {
				id: id
			},
			method : 'post',
			callback : function(options,success,response){
				grid.setLoading(false);
				var localJson = new Ext.decode(response.responseText);
				if(localJson.exceptionInfo){
					showError(localJson.exceptionInfo);return;
				}
				if(localJson.success){
					showResult('提示','删除成功!');
					grid.loadNewStore(grid,grid.params);
				} else {
					delFailure();
				}
			}
		});
	},
	setColumns:function(columns){
		Ext.Array.each(columns,function(column){
			if(column.xtype=='yncolumn'){
				column.xtype='checkcolumn';
				column.editor= {
						xtype: 'checkbox',
						cls: 'x-grid-checkheader-editor'
				};
			}
		});
		return columns;
	},
	DetailUpdateSuccess:function(grid,btn){
		var departgrid=Ext.getCmp('departgrid');
        departgrid.getStore().load();
		var win=btn.up('window');
		if(win) win.close();
	}
})