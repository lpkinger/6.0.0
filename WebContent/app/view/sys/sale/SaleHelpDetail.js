Ext.define('erp.view.sys.sale.SaleHelpDetail', {
        extend: 'Ext.panel.Panel',
        alias: 'widget.salehelpdetail',
        tplMarkup: [
            '<b>{first_name}&nbsp;{last_name}</b>&nbsp;&nbsp;',
            'Title: {title}<br/><br/>',
            '<b>Last Review</b>&nbsp;&nbsp;',
            'Attendance:&nbsp;{attendance}&nbsp;&nbsp;',
            'Attitude:&nbsp;{attitude}&nbsp;&nbsp;',
            'Communication:&nbsp;{communication}&nbsp;&nbsp;',
            'Excellence:&nbsp;{excellence}&nbsp;&nbsp;',
            'Skills:&nbsp;{skills}&nbsp;&nbsp;',
            'Teamwork:&nbsp;{teamwork}' 
        ],
        
        height:90,
        bodyPadding: 7,
        initComponent: function() {
            this.tpl = new Ext.Template(this.tplMarkup);
            erp.view.sys.sale.SaleHelpDetail.superclass.initComponent.call(this);
        }
    });