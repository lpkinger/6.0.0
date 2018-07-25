Ext.define('erp.view.common.init.InitStep', { 
	extend: 'Ext.Viewport',
	style: 'background: #f1f2f5;',
	initComponent : function(){ 
		var me = this;
		Ext.apply(me, {
			items: [{
				xtype: 'panel',
				bodyStyle: 'background: #f1f2f5;',
				width: '100%',
				layout: {
					type: 'hbox',
					pack: 'center'
				},
				items: [ this.createStaticGrid(), this.createDynamicGrid() ],
				buttonAlign: 'center',
				buttons: [{
					text: '上一步',
					cls: 'custom-button',
					name: 'prev'
				}, {
					text: '刷新',
					name: 'refresh',
					cls: 'custom-button'
				}, {
					text: '下一步',
					name: 'next',
					cls: 'custom-button'
				}]
			}] 
		});
		me.callParent(arguments);
	},
	createStaticGrid: function() {
		var me = this;
		Ext.define('Import', {
			extend : 'Ext.data.Model',
			fields : [ {
				name : 'no',
				type : 'number'
			}, {
				name : 'caller',
				type : 'string'
			}, {
				name : 'table',
				type : 'string'
			}, {
				name : 'cond',
				type : 'string'
			}, {
				name : 'module',
				type : 'string'
			}, {
				name : 'data',
				type : 'string'
			}, {
				name : 'count',
				type : 'string'
			}, {
				name : 'method',
				type : 'string'
			}]
		});
		var store = Ext.create('Ext.data.Store', {
			model : 'Import',
			sorters: [{
				property : 'no',
				direction: 'ASC'
	        }],
			data: this._data
		});
		this.staticgrid = Ext.create('Ext.grid.Panel', {
			title: '静态数据',
			bodyStyle: 'background: #f1f1f1;min-height: 450px;',
			cls: 'custom',
			columns: [{
				text: '顺序',
				dataIndex: 'no',
				width: 40,
				tdCls: 'x-grid-cell-special',
				align: 'center'
			}, {
				text: '模块',
				dataIndex: 'module',
				width: 80
			}, {
				text: '数据',
				dataIndex: 'data',
				width: 140
			}, {
				text: '引入方式',
				dataIndex: 'method',
				width: 100
			}, {
				text: '已记录数',
				dataIndex: 'count',
				align: 'right',
				width: 70,
				renderer: function(val, meta, record) {
					if(val == 'loading') {
						meta.tdCls = 'loading';
						return '';
					}
					meta.tdCls = '';
					return val;
				}
			},{
	        	text: '导入',
	        	width: 50,
	        	cls: 'x-grid-header',
	        	renderer: function(val, meta){
	        		meta.tdCls = 'x-form-excel-trigger';
	        		meta.style = 'cursor:pointer;';
	        		return val;
	        	},
	        	processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
	        		if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
	        			var grid = view.ownerCt, record = grid.store.getAt(recordIndex);
	        			me.createTemplate(record);
	        		}
	        		return false;
	        	}
	        }],
			columnLines: true,
			store: store
		});
		return this.staticgrid;
	},
	createDynamicGrid: function() {
		var me = this;
		Ext.define('Import', {
			extend : 'Ext.data.Model',
			fields : [ {
				name : 'no',
				type : 'number'
			}, {
				name : 'caller',
				type : 'string'
			}, {
				name : 'table',
				type : 'string'
			}, {
				name : 'cond',
				type : 'string'
			}, {
				name : 'module',
				type : 'string'
			}, {
				name : 'data',
				type : 'string'
			}, {
				name : 'count',
				type : 'string'
			}, {
				name : 'method',
				type : 'string'
			}]
		});
		var store = Ext.create('Ext.data.Store', {
			model : 'Import',
			sorters: [{
				property : 'no',
				direction: 'ASC'
	        }],
			data: this.__data
		});
		this.dynamicgrid = Ext.create('Ext.grid.Panel', {
			title: '动态数据',
			bodyStyle: 'background: #f1f1f1;min-height: 450px;',
			cls: 'custom',
			name: 'dynamic',
			columns: [{
				text: '顺序',
				dataIndex: 'no',
				width: 40,
				tdCls: 'x-grid-cell-special',
				align: 'center'
			}, {
				text: '模块',
				dataIndex: 'module',
				width: 80
			}, {
				text: '数据',
				dataIndex: 'data',
				width: 140
			}, {
				text: '引入方式',
				dataIndex: 'method',
				width: 100
			}, {
				text: '已记录数',
				dataIndex: 'count',
				align: 'right',
				width: 70,
				renderer: function(val, meta, record) {
					if(val == 'loading') {
						meta.tdCls = 'loading';
						return '';
					}
					meta.tdCls = '';
					return val;
				}
			},{
	        	text: '引入',
	        	width: 50,
	        	cls: 'x-grid-header',
	        	renderer: function(val, meta){
	        		meta.tdCls = 'x-form-excel-trigger';
	        		meta.style = 'cursor:pointer;';
	        		return val;
	        	},
	        	processEvent: function(type, view, cell, recordIndex, cellIndex, e) {
	        		if (type == 'mousedown' || (type == 'keydown' && (e.getKey() == e.ENTER || e.getKey() == e.SPACE))) {
	        			var grid = view.ownerCt, record = grid.store.getAt(recordIndex);
	        			me.createTemplate(record);
	        		}
	        		return false;
	        	}
	        }],
			columnLines: true,
			store: store
		});
		return this.dynamicgrid;
	},
	createTemplate: function(record) {
		var c = record.get('caller');
		if(!c) return;
		Ext.create('Ext.Window', {
			title: record.get('data'),
			width: '80%',
			height: '100%',
			autoShow: true,
			items: [{
				width: '100%',
				height: '100%',
				html: '<iframe src="' + basePath + 'jsps/common/import.jsp?whoami=' + c + '" height="100%" width="100%">'
			}]
		});
	},
	_data: [{
		no: 1,
		type: '静态数据',
		module: '人力资源',
		data: '部门',
		caller: 'Department',
		table: 'Department',
		method: 'excel'
	},{
		no: 2,
		type: '静态数据',
		module: '人力资源',
		data: '组织',
		caller: 'HrOrg',
		table: 'HrOrg',
		method: 'excel'
	},{
		no: 3,
		type: '静态数据',
		module: '人力资源',
		data: '职位',
		caller: 'HrHeadShip',
		table: 'HrHeadShip',
		method: 'excel'
	},{
		no: 4,
		type: '静态数据',
		module: '人力资源',
		data: '岗位',
		caller: 'Job',
		table: 'Job',
		method: 'excel、标准库'
	},{
		no: 5,
		type: '静态数据',
		module: '人力资源',
		data: '人事资料',
		caller: 'Employee',
		table: 'Employee',
		method: 'excel'
	},{
		no: 6,
		type: '静态数据',
		module: '基础资料',
		data: '仓库',
		caller: 'WareHouse',
		table: 'WareHouse',
		method: 'excel'
	},{
		no: 7,
		type: '静态数据',
		module: '基础资料',
		data: '付款方式',
		caller: 'Payments!Purc',
		table: 'Payments',
		cond: 'nvl(pa_forpurchase,0)=1',
		method: 'excel'
	},{
		no: 8,
		type: '静态数据',
		module: '基础资料',
		data: '收款方式',
		caller: 'Payments!Sale',
		table: 'Payments',
		cond: 'nvl(pa_forsale,0)=1',
		method: 'excel'
	},{
		no: 9,
		type: '静态数据',
		module: '基础资料',
		data: '供应商',
		caller: 'Vendor',
		table: 'Vendor',
		method: 'excel'
	},{
		no: 10,
		type: '静态数据',
		module: '基础资料',
		data: '客户',
		caller: 'Customer',
		table: 'Customer',
		method: 'excel'
	},{
		no: 11,
		type: '静态数据',
		module: '基础资料',
		data: '物料编码',
		caller: 'ProductKind',
		table: 'ProductKind',
		method: 'excel'
	},{
		no: 12,
		type: '静态数据',
		module: '基础资料',
		data: '物料',
		caller: 'Product',
		table: 'Product',
		method: 'excel'
	},{
		no: 13,
		type: '静态数据',
		module: '基础资料',
		data: '科目',
		caller: 'Category!Base',
		table: 'Category',
		method: 'excel'
	},{
		no: 14,
		type: '静态数据',
		module: '采购管理',
		data: '采购价格',
		caller: 'PurchasePrice',
		table: 'PurchasePrice',
		method: 'excel'
	},{
		no: 15,
		type: '静态数据',
		module: '销售管理',
		data: '销售价格',
		caller: 'SalePrice',
		table: 'SalePrice',
		method: 'excel'
	},{
		no: 16,
		type: '静态数据',
		module: '生产管理',
		data: 'BOM',
		caller: 'BOM',
		table: 'BOM',
		method: 'excel'
	}],
	__data: [{
		no: 17,
		type: '动态数据',
		module: '采购管理',
		data: '未完成采购单',
		caller: 'Purchase',
		table: 'Purchase',
		method: 'excel'
	},{
		no: 18,
		type: '动态数据',
		module: '销售管理',
		data: '未完成订单',
		caller: 'Sale',
		table: 'Sale',
		method: 'excel'
	},{
		no: 19,
		type: '动态数据',
		module: '生产管理',
		data: '未完成工单',
		table: 'Make',
		cond: 'ma_tasktype=\'MAKE\'',
		method: 'excel'
	},{
		no: 20,
		type: '动态数据',
		module: '生产管理',
		data: '未完成委外工单',
		table: 'Make',
		cond: 'ma_tasktype=\'OS\'',
		method: 'excel'
	},{
		no: 21,
		type: '动态数据',
		module: '供应链',
		data: '初始库存',
		caller: 'ProdInOut',
		table: 'ProdInOut',
		cond: 'pi_class=\'库存初始化\'',
		method: 'excel'
	},{
		no: 22,
		type: '动态数据',
		module: '供应链',
		data: '初始暂估',
		caller: 'Estimate',
		table: 'Estimate',
		method: 'excel'
	},{
		no: 23,
		type: '动态数据',
		module: '供应链',
		data: '初始发出商品',
		caller: 'GoodsSend',
		table: 'GoodsSend',
		method: 'excel'
	},{
		no: 24,
		type: '动态数据',
		module: '总账',
		data: '科目余额',
		caller: 'LedgerInit',
		table: 'LedgerInit',
		method: 'excel'
	},{
		no: 25,
		type: '动态数据',
		module: '总账',
		data: '辅助核算余额',
		caller: 'LedgerInitDetail',
		table: 'LedgerInitDetail',
		method: 'excel'
	},{
		no: 26,
		type: '动态数据',
		module: '应收款',
		data: '应收发票',
		caller: 'ARBill',
		table: 'ARBill',
		cond: 'ab_class=\'初始化\'',
		method: 'excel'
	},{
		no: 27,
		type: '动态数据',
		module: '应收款',
		data: '预收',
		caller: 'PreRec',
		table: 'PreRec',
		cond: 'pr_kind=\'初始化\'',
		method: 'excel'
	},{
		no: 28,
		type: '动态数据',
		module: '应付款',
		data: '应付发票',
		caller: 'APBill',
		table: 'APBill',
		cond: 'ab_class=\'初始化\'',
		method: 'excel'
	},{
		no: 29,
		type: '动态数据',
		module: '应付款',
		data: '预付',
		caller: 'PrePay',
		table: 'PrePay',
		cond: 'pp_type=\'初始化\'',
		method: 'excel'
	},{
		no: 30,
		type: '动态数据',
		module: '固定资产',
		caller: 'AssetsCard',
		data: '初始固定资产卡片',
		table: 'AssetsCard',
		method: 'excel'
	}]
});