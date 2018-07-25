Ext.define('erp.view.scm.reserve.WarehouseingQuery',{ 
	extend: 'Ext.Viewport', 
	layout: 'anchor', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
	    		xtype:'form',
	    		id: 'form',
	    		layout:'column',
	    		anchor : '100% 15%',
	    		frame:true,
	    		defaults:{
	    			margin: '5 15 0 5',
	    			xtype:'textfield',
	    			columnWidth:1/3
	    		},
	    		items:[{
	    			xtype: 'dbfindtrigger',
	    			fieldLabel:'入仓单号',
	    			emptyText:'请录入入仓单号',
	    			labelStyle:"color:red;",
	    			id:'whi_code',
	    			name:'whi_code',
	    			allowBlank: true,
	    			listeners:{
						aftertrigger:function(f, d){
							f.setValue(d.data.whi_code);
						}
					}
	    		},{
	    			xtype: 'combo',
	    			fieldLabel:'当前状态',
	    			id:'whi_status',
	    			name:'whi_status',
	    			store: {
						fields: ['dlc_display', 'dlc_value'],
						data :[],
					},
					displayField: 'dlc_display',
					valueField: 'dlc_value', 
					onTriggerClick:function(trigger){ 
						var combodata=me.getComboData('Warehouseing','whi_status');
						//这里写方法查找combo的数据
						this.getStore().loadData(combodata);
						this.expand(); 
					},
					editable: false
	    		},{
	    			xtype: 'textareatrigger',
	    		    fieldLabel:'描述',
	    		    readOnly: false,
	    		    id:'whi_text',
	    			name:'whi_text'
	    		}],
	    		buttonAlign: 'center',
				buttons: [{
					xtype: 'erpQueryButton'
				}, {
					xtype: 'erpUpdateButton'
				}, {
					xtype: 'erpCloseButton'
				}]
		    },{
		    	xtype : 'grid',
				anchor: '100% 85%',
				columnLines : true,
				id : 'grid',
				plugins: [Ext.create('erp.view.core.grid.HeaderFilter', {
					remoteFilter: false
				}),Ext.create('Ext.grid.plugin.CellEditing', {
					clicksToEdit: 1,
				})],
				columns : [ {
					text : '入仓单号',
					cls : 'x-grid-header-1',
					dataIndex: 'WHL_CODE',
					width: 180,
					filter: {
	    				xtype : 'textfield'
	    			}
				},{
					text : '状态',
					cls : 'x-grid-header-1',
					dataIndex: 'WHL_STATUS',
					width: 120,
					filter: {
	    				xtype : 'textfield'
	    			}
				},{
					text : '更新日期',
					cls : 'x-grid-header-1',
					dataIndex: 'WHL_UPDATEDATE',
					width: 250,
					filter: {
	    				xtype : 'datefield'
	    			},
	    			renderer: function(val) {return Ext.Date.format(new Date(val), 'Y-m-d H:i:s');}
				}, {
					text : '更新人',
					cls : 'x-grid-header-1',
					dataIndex: 'WHL_UPDATEMAN',
					width: 80,
					filter: {
	    				xtype : 'textfield'
	    			}
				}, {
					text : '描述',
					cls : 'x-grid-header-1',
					dataIndex: 'WHL_TEXT',
					width: 380,
					filter: {
	    				xtype : 'textfield'
	    			}
				}],
				store : new Ext.data.Store({
					fields : [ 'WHL_CODE', 'WHL_UPDATEDATE', 'WHL_STATUS',
							'WHL_UPDATEMAN', 'WHL_ID','WHL_TEXT' ],
					proxy : {
						type : 'ajax',
						url : basePath + 'scm/reserve/getWarehouseingLog.action',
						reader : {
							type : 'json',
							root : 'data'
						}
					},
					autoLoad : false
				})
		    }]
		}); 
		me.callParent(arguments); 
	},
	getComboData:function(caller,field){
		var combodata=null;
		Ext.Ajax.request({
			url : basePath +'common/getComboDataByCallerAndField.action',
			params: {
				caller:caller,
				field:field
			},
			async: false,
			method : 'post',
			callback : function(options,success,response){
				var res = new Ext.decode(response.responseText);
				if(res.exceptionInfo != null){
					showError(res.exceptionInfo);
					return;
				}
				if(res.success){
					combodata=res.data;
				}
			} 
		});
		if(combodata.length<1){
			this.add10EmptyData(combodata,caller, field);
		}
		return combodata;
	}
});