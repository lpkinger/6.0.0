/**
*两个字段合并成一个字段显示
*/
Ext.define('erp.view.core.form.DoubleField', {
    extend: 'Ext.form.field.Base',
    alias: 'widget.doublefield',
    height: 22,
    firstname: '',//第一个字段
    firstxtype: '',
    secondname: '',//另外的一个字段
    secondxtype: '',
    fieldSubTpl: [
                   '<input id="{id}" type="{type}" ',
                   '<tpl if="name">name="{name}" </tpl>',
                   '<tpl if="size">size="{size}" </tpl>',
                   '<tpl if="tabIdx">tabIndex="{tabIdx}" </tpl>',
                   'class="{fieldCls} {typeCls}" autocomplete="off" />',
                   {
                       compiled: true,
                       disableFormats: true
                   }
               ],
    initComponent : function(){ 
    	console.log(this);
    	this.callParent(arguments);
        this.items =  [{xtype: this.firstxtype, name: this.firstname, width: this.width*0.4, allowBlank: true},
                     {xtype: 'displayfield', value: '('},
                     {xtype: 'textfield', name: this.secondname, width: this.width*0.4, allowBlank: true},
                     {xtype: 'displayfield', value: ')'}];
    }
});