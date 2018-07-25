Ext.define('erp.view.common.init.CheckBase', { 
	extend: 'Ext.Viewport', 
	layout: {
		type: 'hbox',
		align: 'middle',
		pack: 'center'
	},
	style: 'background: #f1f2f5;',
	initComponent : function(){ 
		var me = this; 
		Ext.apply(me, {
			items: [{
				xtype: 'form',
				height: 400,
				width: 500,
				layout: 'vbox',
				bodyStyle: 'background: #f1f1f1;',
				items: [{
					xtype: 'progressbar',
					text: '检测基础配置及基础资料设置',
					width: 500,
					value: 1,
					height: 20
				},{
					xtype: 'fieldcontainer',
					layout: 'column',
					margin: '4 0 0 4',
					width: 500,
					defaults: {
						columnWidth: .25
					},
					items: [{
						xtype: 'checkbox',
						checked: true,
						boxLabel: '基础配置项(使用前需具备数据)',
						columnWidth: 1
					},{
						xtype: 'displayfield',
						value: '基础资料表',
						table: 'BaseDataSet'
					},{
						xtype: 'displayfield',
						value: '列表',
						table: 'Datalist'
					},{
						xtype: 'displayfield',
						value: '列表明细',
						table: 'DatalistDetail'
					},{
						xtype: 'displayfield',
						value: '表单dbfind',
						table: 'DbfindsetUI'
					},{
						xtype: 'displayfield',
						value: '表格dbfind',
						table: 'Dbfindset'
					},{
						xtype: 'displayfield',
						value: '表格dbfind明细',
						table: 'DbfindsetDetail'
					},{
						xtype: 'displayfield',
						value: '表格dbfind对照关系',
						table: 'DbfindsetGrid'
					},{
						xtype: 'displayfield',
						value: '下拉框',
						table: 'DatalistCombo'
					},{
						xtype: 'displayfield',
						value: '表格',
						table: 'DetailGrid'
					},{
						xtype: 'displayfield',
						value: '逻辑配置明细',
						table: 'DocumentHandler'
					},{
						xtype: 'displayfield',
						value: '出入库单设置',
						table: 'DocumentSetup'
					},{
						xtype: 'displayfield',
						value: '表单',
						table: 'Form'
					},{
						xtype: 'displayfield',
						value: '表单明细',
						table: 'FormDetail'
					},{
						xtype: 'displayfield',
						value: '表格按钮',
						table: 'GridButton'
					},{
						xtype: 'displayfield',
						value: '初始化表',
						table: 'Initialize'
					},{
						xtype: 'displayfield',
						value: '初始化明细表',
						table: 'InitDetail'
					},{
						xtype: 'displayfield',
						value: '算法设计',
						table: 'LogicDesc'
					},{
						xtype: 'displayfield',
						value: '逻辑配置',
						table: 'LogicSetup'
					},{
						xtype: 'displayfield',
						value: '抛转公式',
						table: 'PostStyle'
					},{
						xtype: 'displayfield',
						value: '抛转关系对照表',
						table: 'PostStyleDetail'
					},{
						xtype: 'displayfield',
						value: '抛转步骤',
						table: 'PostStyleStep'
					},{
						xtype: 'displayfield',
						value: '表单关联查询',
						table: 'RelativeSearch'
					},{
						xtype: 'displayfield',
						value: '表单关联查询Form',
						table: 'RelativeSearchForm'
					},{
						xtype: 'displayfield',
						value: '表单关联查询Grid',
						table: 'RelativeSearchGrid'
					},{
						xtype: 'displayfield',
						value: '状态码对照表',
						table: 'Status'
					},{
						xtype: 'displayfield',
						value: '导航',
						table: 'Sysnavigation'
					},{
						xtype: 'displayfield',
						value: '特殊权限库',
						table: 'SysSpecialPower'
					},{
						xtype: 'displayfield',
						value: '关联UU字段',
						table: 'UUListener'
					},{
						xtype: 'checkbox',
						checked: true,
						boxLabel: '基础数据(业务处理前需具备数据)',
						margin: '4 0 0 0',
						columnWidth: 1
					},{
						xtype: 'displayfield',
						value: '辅助核算',
						table: 'AssKind'
					},{
						xtype: 'displayfield',
						value: '币别',
						table: 'Currencys'
					},{
						xtype: 'displayfield',
						value: '月度汇率',
						table: 'CurrencysMonth'
					},{
						xtype: 'displayfield',
						value: '编号原则',
						table: 'MaxNumbers'
					},{
						xtype: 'displayfield',
						value: '账期',
						table: 'Periods'
					},{
						xtype: 'displayfield',
						value: '账期明细',
						table: 'PeriodsDetail'
					},{
						xtype: 'displayfield',
						value: '系统设置',
						table: 'Setting'
					},{
						xtype: 'displayfield',
						value: '标准岗位',
						table: 'ST_Job'
					},{
						xtype: 'displayfield',
						value: '标准岗位权限',
						table: 'ST_PositionPower'
					}]
				}],
				buttonAlign: 'center',
				buttons: [{
					text: '上一步',
					cls: 'custom-button',
					name: 'prev'
				},{
					text: '检测',
					name: 'check',
					cls: 'custom-button'
				}, {
					disabled: true,
					text: '一键修补',
					name: 'repair',
					cls: 'custom-button'
				}, {
					disabled: true,
					text: '下一步',
					name: 'next',
					cls: 'custom-button'
				}]
			}] 
		});
		me.callParent(arguments); 
	}
});