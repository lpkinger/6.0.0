Ext.define('erp.view.ma.Conf',{ 
	extend: 'Ext.Viewport', 
	layout: 'border', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, { 
			items: [{
				region: 'north',
				layout: {
					type: 'hbox',
					pack: 'center'
				},
				bodyStyle: 'background: #f1f1f1;',
				defaults: {
					margin: '3 10 3 0'
				},
				items: [ {
					xtype: 'button',
					text: '差异分析',
					cls: 'x-btn-blue',
					width: 100,
					id: 'condition'
				}, {
					xtype: 'button',
					text: '其它配置项',
					cls: 'x-btn-blue',
					width: 100,
					id: 'others'
				}, {
					xtype: 'button',
					text: '关闭',
					cls: 'x-btn-blue',
					width: 60
				}, {
					xtype: 'displayfield',
					id: 'logger',
					margin: '3 2 3 3',
					width: 110
				}, {
					xtype: 'button',
					text: '一键同步?',
					cls: 'x-btn-blue',
					margin: '3 10 3 0',
					width: 80,
					id: 'sync',
					hidden: true
				}]
			}, {
				region: 'center',
				scrollable: true,
				autoScroll: true,
				defaults: {
					xtype: 'gridpanel',
					width: '100%',
					collapsible: true,
					columns: [{
						text: '配置项',
						dataIndex: 'conf',
						flex: 1,
						renderer: function(val, meta) {
					        meta.tdCls = Ext.baseCSSPrefix + 'grid-cell-special';
					        return val;
					    }
					},{
						text: '优软标准配置',
						dataIndex: 'usoft',
						flex: 4
					}, {
						text: '当前账套配置',
						dataIndex: 'current',
						flex: 4
					}, {
						text: '结果',
						dataIndex: 'result',
						flex: 0.5,
						renderer: function(val, meta, record) {
							if(Ext.isEmpty(record.get('usoft')) && Ext.isEmpty(record.get('current'))) {
								meta.tdCls = '';
							} else if((val > 1) || (record.get('usoft') != record.get('current'))){
								meta.tdCls = 'error';
								val = val == null ? 1 : val;
								if(val > 1)
									return '<font color=red>' + val + '</font>个差异';
							} else {
								meta.tdCls = 'checked';
							}
							return '';
						}
					}],
					columnLines: true,
					viewConfig: {
				        getRowClass: function(record) {
				            return !Ext.isEmpty(record.get('result')) ? 'custom-alt' : '';
				        } 
				    }
				},
				items: [{
					title: '导航栏设置',
					id: 'navigation',
					collapsed: false,
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '描述'
						},{
							conf: '链接'
						},{
							conf: '序号'
						},{
							conf: '是否叶节点'
						},{
							conf: '显示模式'
						},{
							conf: '允许删除'
						},{
							conf: '是否启用'
						},{
							conf: '允许扩展逻辑'
						},{
							conf: 'Caller'
						}]
					})
				}, {
					title: 'Form配置',
					id: 'form',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						},{
							conf: 'Caller'
						},{
							conf: '表名'
						},{
							conf: '界面名称'
						},{
							conf: '主表关键字'
						},{
							conf: 'Code字段'
						},{
							conf: '明细表名'
						},{
							conf: '明细关联字段'
						},{
							conf: '明细orderby'
						},{
							conf: '明细序号字段'
						},{
							conf: 'Buttons(新增)'
						},{
							conf: 'Buttons(读写)'
						},{
							conf: '审批流Caller'
						},{
							conf: '状态字段'
						},{
							conf: '状态码字段'
						},{
							conf: '明细状态'
						},{
							conf: '明细状态码'
						},{
							conf: '录入人字段'
						},{
							conf: '提交触发流程'
						}]
					})
				}, {
					title: 'Grid配置',
					id: 'detailgrid',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: 'Form Dbfind配置',
					id: 'dbfindsetui',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: 'Grid Dbfind配置',
					id: 'dbfindset',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						},{
							conf: 'Caller'
						},{
							conf: '表名'
						},{
							conf: '描述'
						},{
							conf: '分组SQL'
						},{
							conf: '排序SQL'
						},{
							conf: '筛选条件'
						}]
					})
				}, {
					title: 'Grid Dbfind对应关系',
					id: 'dbfindsetgrid',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: '下拉框配置',
					id: 'datalistcombo',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: 'Caller'
						},{
							conf: '字段'
						},{
							conf: '显示值'
						},{
							conf: '实际值'
						}]
					})
				}, {
					title: 'DataList配置',
					id: 'datalist',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						},{
							conf: 'Caller'
						},{
							conf: '表名'
						},{
							conf: '跳转页面'
						},{
							conf: 'SQL条件'
						},{
							conf: 'groupby语句'
						},{
							conf: 'orderby语句'
						},{
							conf: '主键字段'
						},{
							conf: '录入人字段'
						},{
							conf: '明细外键'
						},{
							conf: '关联列表Caller'
						}]
					})
				}, {
					title: '关联表结构',
					collapsed: false,
					id: 'table',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: '关联触发器',
					collapsed: false,
					id: 'trigger',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: '关联索引',
					collapsed: false,
					id: 'index',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: '数据字典配置',
					collapsed: false,
					id: 'datadictionary',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: 'Grid按钮配置',
					collapsed: false,
					id: 'gridbutton',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: 'UU字段',
					collapsed: false,
					id: 'uulistener',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}, {
					title: '逻辑配置',
					collapsed: false,
					id: 'logicsetup',
					store: new Ext.data.Store({
						fields: ['conf', 'usoft', 'current'],
						data: [{
							conf: '详细'
						}]
					})
				}]
			}] 
		}); 
		me.callParent(arguments); 
	} 
});