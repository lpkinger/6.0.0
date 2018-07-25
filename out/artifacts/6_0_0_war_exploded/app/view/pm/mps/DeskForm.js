Ext.define('erp.view.pm.mps.DeskForm', {
    extend: 'Ext.form.Panel',
    requires: ['erp.view.core.button.OdDynamicAnalysis'],
    alias: 'widget.erpMPSDeskFormPanel',
    id:'mpsdeskform',
    frame: true,
    layout: 'column',
    autoScroll: true,
    defaultType: 'textfield',
    labelSeparator: ':',
    FormUtil: Ext.create('erp.util.FormUtil'),
    GridUtil: Ext.create('erp.util.GridUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
    buttonAlign: 'center',
    buttons: [{
        text: '物料',
        id: 'product',
        style: {
            marginBottom: '20px'
        }
    },
    {
        text: '供应',
        id: 'supply'
    },
    {
        text: '需求',
        id: 'need',
        disabled: true
    },
    {
        text: '清理无效PR',
        id: 'topurchase'
    },
    {
        text: '清理无效预测',
        id: 'topurchaseforecast'
    },
    {
        text: '转制造',
        id: 'tomake'
    }/*,{
        text: '呆滞库存处理',
        id: 'dullstockdeal'
    }*/,{
        text: '异常报告',
        id: 'error'
    },
    {
        xtype: 'erpOrderAnalysisButton'
    },{
    	text: '剩余供应ECN分析',
        id: 'ecnAnalysis'
    },
    {
        text: '关闭',
        id: 'close'
    }],
    initComponent: function () {
        this.callParent(arguments);
    }
});