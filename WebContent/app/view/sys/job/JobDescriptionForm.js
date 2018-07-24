Ext.define('erp.view.sys.job.JobDescriptionForm',{
	extend:'Ext.form.Panel',
	alias:'widget.jobdescriptionform',
	width: 400,
    bodyPadding: 10,
    items: [{
        xtype     : 'textareafield',
        grow      : true,
        name      : 'message',
        fieldLabel: 'Message',
        anchor    : '100%'
    }]
})