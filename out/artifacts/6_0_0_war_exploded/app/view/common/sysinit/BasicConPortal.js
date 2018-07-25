Ext.define('erp.view.common.sysinit.BasicConPortal', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.basicconportlet',
	layout: 'column',
	margin: '4 0 0 4',
	autoScroll:true,
	border:false,
	defaults: {
		xtype: 'displayfield',
		columnWidth: .25
	},
	items: [{
		xtype: 'checkbox',
		checked: true,
		boxLabel: '基础配置项(使用前需具备数据)',
		columnWidth: 1
	},{
		value: '基础资料表',
		table: 'BaseDataSet'
	},{
		value: '列表',
		table: 'Datalist'
	},{
		value: '列表明细',
		table: 'DatalistDetail'
	},{
		value: '表单dbfind',
		table: 'DbfindsetUI'
	},{
		value: '表格dbfind',
		table: 'Dbfindset'
	},{
		value: '表格dbfind明细',
		table: 'DbfindsetDetail'
	},{
		value: '表格dbfind对照关系',
		table: 'DbfindsetGrid'
	},{
		value: '下拉框',
		table: 'DatalistCombo'
	},{
		value: '表格',
		table: 'DetailGrid'
	},{
		value: '逻辑配置明细',
		table: 'DocumentHandler'
	},{
		value: '出入库单设置',
		table: 'DocumentSetup'
	},{
		value: '表单',
		table: 'Form'
	},{
		value: '表单明细',
		table: 'FormDetail'
	},{
		value: '表格按钮',
		table: 'GridButton'
	},{
		value: '初始化表',
		table: 'Initialize'
	},{
		value: '初始化明细表',
		table: 'InitDetail'
	},{
		value: '算法设计',
		table: 'LogicDesc'
	},{
		value: '逻辑配置',
		table: 'LogicSetup'
	},{
		value: '抛转公式',
		table: 'PostStyle'
	},{
		value: '抛转关系对照表',
		table: 'PostStyleDetail'
	},{
		value: '抛转步骤',
		table: 'PostStyleStep'
	},{
		value: '表单关联查询',
		table: 'RelativeSearch'
	},{
		value: '表单关联查询Form',
		table: 'RelativeSearchForm'
	},{
		value: '表单关联查询Grid',
		table: 'RelativeSearchGrid'
	},{
		value: '状态码对照表',
		table: 'Status'
	},{
		value: '导航',
		table: 'Sysnavigation'
	},{
		value: '特殊权限库',
		table: 'SysSpecialPower'
	},{
		value: '关联UU字段',
		table: 'UUListener'
	},{
		xtype: 'checkbox',
		checked: true,
		boxLabel: '基础数据(业务处理前需具备数据)',
		margin: '4 0 0 0',
		columnWidth: 1
	},{
		value: '辅助核算',
		table: 'AssKind'
	},{
		value: '币别',
		table: 'Currencys'
	},{
		value: '月度汇率',
		table: 'CurrencysMonth'
	},{
		value: '编号原则',
		table: 'MaxNumbers'
	},{
		value: '账期',
		table: 'Periods'
	},{
		value: '账期明细',
		table: 'PeriodsDetail'
	},{
		value: '系统设置',
		table: 'Setting'
	},{
		value: '标准岗位',
		table: 'ST_Job'
	},{
		value: '标准岗位权限',
		table: 'ST_PositionPower'
	}],
    initComponent: function(){
        this.callParent(arguments);
    }
});
