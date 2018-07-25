//MultiDatePicker  
Ext.define('erp.view.core.picker.HighlightableDatePicker', {
    extend: 'Ext.picker.Date',
    alias: "widget.highlightdate",
    clsHigligthClass:'x-datepicker-selected',
    
    selectedDates: null,
    
    constructor: function(args){
        this.callParent([Ext.applyIf(args||{}, {
            selectedDates: {}
        })]);   
    },
    
    initComponent: function(){
        var me = this;
        me.callParent(arguments);
        me.on('select',me.handleSelectionChanged,me);
        me.on('afterrender',me.higlighDates,me);
    },
    
    showPrevMonth: function(e){
        var me = this; 
        var c = this.update(Ext.Date.add(this.activeDate, Ext.Date.MONTH, -1));
        me.higlighDates();
        return c;
    },
    
    showNextMonth: function(e){
        var me = this; 
        var c = this.update(Ext.Date.add(this.activeDate, Ext.Date.MONTH, 1));
        me.higlighDates();
        return c;
    },
    
    higlighDates: function(){
        var me = this; 
        if(!me.cells) return;
        me.cells.each(function(item){
            var date = new Date(item.dom.firstChild.dateValue).toDateString();
            if(me.selectedDates[date]){
                item.addCls(me.clsHigligthClass);
            }else{
                item.removeCls(me.clsHigligthClass);
            }
        });
    },
    
    handleSelectionChanged: function(cmp, date){
        var me = this;
        if(me.selectedDates[date.toDateString()])
            delete me.selectedDates[date.toDateString()];
            else
                me.selectedDates[date.toDateString()] = date;
        me.higlighDates();
    },
    
    getSelectedDates: function(){
        var dates = [];
        Ext.iterate(this.selectedDates, function(key, val){
            dates.push(val);
        });
        dates.sort();
        return dates;
    }
});

