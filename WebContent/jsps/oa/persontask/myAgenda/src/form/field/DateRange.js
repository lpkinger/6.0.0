/**
 * @class Ext.form.field.DateRange
 * @extends Ext.form.Field
 * <p>A combination field that includes start and end dates and times, as well as an optional all-day checkbox.</p>
 * @constructor
 * @param {Object} config The config object
 */
Ext.define('Ext.calendar.form.field.DateRange', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.daterangefield',
    layout:'hbox',
    requires: [
        'Ext.form.field.Date',
        'Ext.form.field.Time',
        'Ext.form.Label',
        'Ext.form.field.Checkbox',
        'Ext.layout.container.Column'
    ],
    
    /**
     * @cfg {String} toText
     * The text to display in between the date/time fields (defaults to 'to')
     */
    toText: '~',
    /**
     * @cfg {String} allDayText
     * The text to display as the label for the all day checkbox (defaults to 'All day')
     */
    allDayText: '全天',
    /**
     * @cfg {String/Boolean} singleLine
     * `true` to render the fields all on one line, `false` to break the start date/time and end date/time
     * into two stacked rows of fields to preserve horizontal space (defaults to `true`).
     */
    singleLine: true,
    /**
     * @cfg {String} dateFormat
     * The date display format used by the date fields (defaults to 'n/j/Y') 
     */
    dateFormat: 'n/j/Y',
    /**
     * @cfg {String} timeFormat
     * The time display format used by the time fields. By default the DateRange uses the
     * {@link Ext.Date.use24HourTime} setting and sets the format to 'g:i A' for 12-hour time (e.g., 1:30 PM) 
     * or 'G:i' for 24-hour time (e.g., 13:30). This can also be overridden by a static format string if desired.
     */
    timeFormat: Ext.Date.use24HourTime ? 'G:i' : 'g:i A',
    baseData:null,
    // private
    fieldLayout: {
        type: 'hbox',
        defaultMargins: { top: 0, right: 5, bottom: 0, left: 0 }
    },
    
    // private
    initComponent: function() {
        var me = this;
        
        me.addCls('ext-dt-range');
        
        if (me.singleLine) {
            me.layout = me.fieldLayout;
            me.items = me.getFieldConfigs();
        }
        else {
            me.items = [{
                xtype: 'container',
                layout: me.fieldLayout,
                items: [
                    me.getStartDateConfig(),
                    me.getStartTimeConfig(),
                    me.getDateSeparatorConfig()
                ]
            },{
                xtype: 'container',
                layout: me.fieldLayout,
                items: [
                    me.getEndDateConfig(),
                    me.getEndTimeConfig(),
                    me.getTimeSetConfig(),
                    me.getAllDayConfig()
                ]
            }];
        }
        
        me.callParent(arguments);
        me.initRefs();
    },
    
    initRefs: function() {
        var me = this;
        me.startDate = me.down('#' + me.id + '-start-date');
        me.startTime = me.down('#' + me.id + '-start-time');
        me.endTime = me.down('#' + me.id + '-end-time');
        me.endDate = me.down('#' + me.id + '-end-date');
        me.allDay = me.down('#' + me.id + '-allday');
        me.toLabel = me.down('#' + me.id + '-to-label');
        me.timeSet = me.down('#' + me.id + '-timeset');
        me.startDate.validateOnChange = me.endDate.validateOnChange = false,

        me.startDate.isValid = me.endDate.isValid = function() {
                                    var me = this,
                                        valid = Ext.isDate(me.getValue());
                                    if (!valid) {
                                        me.focus();
                                    }
                                    return valid;
                                 };
    },

    getFieldConfigs: function() {
        var me = this;
        return [
            me.getStartDateConfig(),
            me.getStartTimeConfig(),
            me.getDateSeparatorConfig(),
            me.getEndTimeConfig(),
            me.getEndDateConfig(),
            me.getTimeSetConfig(),
            me.getAllDayConfig()
        ];
    },
    
    getStartDateConfig: function() {
        return {
            xtype: 'datefield',
            itemId: this.id + '-start-date',
            format: this.dateFormat,
            width: 100,
            listeners: {
                'blur': {
                    fn: function(){
                        this.onFieldChange('date', 'start');
                    },
                    scope: this
                }
            }
        };
    },
    
    getStartTimeConfig: function() {
        return {
            xtype: 'timefield',
            itemId: this.id + '-start-time',
            hidden: this.showTimes === false,
            labelWidth: 0,
            hideLabel: true,
            width: 90,
            format: this.timeFormat,
            listeners: {
                'select': {
                    fn: function(){
                        this.onFieldChange('time', 'start');
                    },
                    scope: this
                }
            }
        };
    },
    
    getEndDateConfig: function() {
        return {
            xtype: 'datefield',
            itemId: this.id + '-end-date',
            format: this.dateFormat,
            hideLabel: true,
            width: 100,
            listeners: {
                'blur': {
                    fn: function(){
                        this.onFieldChange('date', 'end');
                    },
                    scope: this
                }
            }
        };
    },
    
    getEndTimeConfig: function() {
        return {
            xtype: 'timefield',
            itemId: this.id + '-end-time',
            hidden: this.showTimes === false,
            labelWidth: 0,
            hideLabel: true,
            width: 90,
            format: this.timeFormat,
            listeners: {
                'select': {
                    fn: function(){
                        this.onFieldChange('time', 'end');
                    },
                    scope: this
                }
            }
        };
    },

    getDuration: function() {
        var me = this,
            start = me.getDT('start'),
            end = me.getDT('end');

        return end.getTime() - start.getTime();
    },
    getTimeSetConfig:function(){
    	return {
            xtype: 'checkbox',
            itemId: this.id + '-allday',
            /*hidden: this.showTimes === false || this.showAllDay === false,*/
            hidden:true,
            boxLabel: this.allDayText,
            scope: this
        }
    },
    getAllDayConfig: function() {
       return {
  		 xtype: 'combo',
		 queryMode: 'local',
		 displayField: 'displayfield',
		 valueField: 'valuefield',
		 width: 80,
		 itemId: this.id + '-timeset',
		 allowBlank:false,
		 store:Ext.create('Ext.data.Store', {
			    fields: ['valuefield', 'displayfield'],
			    data : [
			        {"valuefield":"1", "displayfield":"上午"},
			        {"valuefield":"2", "displayfield":"下午"},
			        {"valuefield":"3", "displayfield":"全天"}
			    ]
		 }),
         boxLabel: this.allDayText,
         defaultValue:'1',
         listeners:{
        	 change:function(field,newValue,oldValue){
        		 this.onAllDayChange(newValue);
        	 },
        	  scope: this
         }
       }
    },
    onAllDayChange: function(v) {
		    var me = this;
    	    v = Number(v);
    		var now=new Date(),
    		minDate,
    		maxDate,
    		minTime,
    		maxTime;
    
    	    this.baseData=this.baseData==null?this.getSystemSet():this.baseData;
    		if(this.baseData.as_amstarttime){
    			me.defaultamstarttime=this.baseData.as_amstarttime;
    			me.defaultamendtime=this.baseData.as_amendtime;
    			me.defaultpmstarttime=this.baseData.as_pmstarttime;
    			me.defaultpmendtime=this.baseData.as_pmendtime;
    		}
    		switch (v) {
    		case 1://上午
    			minDate = now;
    			maxDate = now;
    			minTime =me.defaultamstarttime;
    			maxTime =me.defaultamendtime;
    			break;
    		case 2://下午
    			minDate = now;
    			maxDate = now;
    			minTime =me.defaultpmstarttime;
    			maxTime =me.defaultpmendtime;
    			break;
    		case 3://整天
    			minDate = now;
    			maxDate = now;
    			minTime =me.defaultamstarttime;
    			maxTime =me.defaultpmendtime;
    			break;
    		case 4://非整天
    			minDate = null;
    			maxDate = null;
    			minTime = null;
    			maxTime = null;
    			break;
    		default:
    		minDate = null;
    		maxDate = null;
    		minTime = null;
    		maxTime = null;
    		break;
    		}
    		if(me.startDate==null){
    			me.startDate.setValue(minDate);
    		     me.endDate.setValue(maxDate);
    		}    		
            me.startTime.setValue(minTime);       
            me.endTime.setValue(maxTime);
            me.allDay.setValue(v==3);
    	},
    
    getDateSeparatorConfig: function() {
        return {
            xtype: 'label',
            itemId: this.id + '-to-label',
            text: this.toText,
            margins: { top: 4, right: 5, bottom: 0, left: 0 }
        };
    },
    
    isSingleLine: function() {
        var me = this;
        
        if (me.calculatedSingleLine === undefined) {
            if(me.singleLine == 'auto'){
                var ownerCtEl = me.ownerCt.getEl(),
                    w = me.ownerCt.getWidth() - ownerCtEl.getPadding('lr'),
                    el = ownerCtEl.down('.x-panel-body');
                    
                if(el){
                    w -= el.getPadding('lr');
                }
                
                el = ownerCtEl.down('.x-form-item-label')
                if(el){
                    w -= el.getWidth() - el.getPadding('lr');
                }
                me.calculatedSingleLine = w <= me.singleLineMinWidth ? false : true;
            }
            else {
                me.calculatedSingleLine = me.singleLine !== undefined ? me.singleLine : true;
            }
        }
        return me.calculatedSingleLine;
    },

    // private
    onFieldChange: function(type, startend){
        this.checkDates(type, startend);
        this.fireEvent('change', this, this.getValue());
    },
        
    // private
    checkDates: function(type, startend){
        var me = this,
            startField = me.down('#' + me.id + '-start-' + type),
            endField = me.down('#' + me.id + '-end-' + type),
            startValue = me.getDT('start'),
            endValue = me.getDT('end');

        if (!startValue || !endValue) {
            return;
        }

        if(startValue > endValue){
            if(startend=='start'){
                endField.setValue(startValue);
            }else{
                startField.setValue(endValue);
                me.checkDates(type, 'start');
            }
        }
        if(type=='date'){
            me.checkDates('time', startend);
        }
    },
    
    /**
     * Returns an array containing the following values in order:<div class="mdetail-params"><ul>
     * <li><b><code>DateTime</code></b> : <div class="sub-desc">The start date/time</div></li>
     * <li><b><code>DateTime</code></b> : <div class="sub-desc">The end date/time</div></li>
     * <li><b><code>Boolean</code></b> : <div class="sub-desc">True if the dates are all-day, false 
     * if the time values should be used</div></li><ul></div>
     * @return {Array} The array of return values
     */
    getValue: function(){
        return [
            this.getDT('start'), 
            this.getDT('end'),
            this.allDay.getValue(),
            this.timeSet.getValue()
        ];
    },
    
    // private getValue helper
    getDT: function(startend){
        var time = this[startend+'Time'].getValue(),
            dt = this[startend+'Date'].getValue();
            
        if(Ext.isDate(dt)){
            dt = Ext.Date.format(dt, this[startend + 'Date'].format);
        }
        else{
            return null;
        };
        if(time && time != ''){
            time = Ext.Date.format(time, this[startend+'Time'].format);
            var val = Ext.Date.parseDate(dt + ' ' + time, this[startend+'Date'].format + ' ' + this[startend+'Time'].format);
            return val;
            //return Ext.Date.parseDate(dt+' '+time, this[startend+'Date'].format+' '+this[startend+'Time'].format);
        }
        return Ext.Date.parseDate(dt, this[startend+'Date'].format);
        
    },
    
    /**
     * Sets the values to use in the date range.
     * @param {Array/Date/Object} v The value(s) to set into the field. Valid types are as follows:<div class="mdetail-params"><ul>
     * <li><b><code>Array</code></b> : <div class="sub-desc">An array containing, in order, a start date, end date and all-day flag.
     * This array should exactly match the return type as specified by {@link #getValue}.</div></li>
     * <li><b><code>DateTime</code></b> : <div class="sub-desc">A single Date object, which will be used for both the start and
     * end dates in the range.  The all-day flag will be defaulted to false.</div></li>
     * <li><b><code>Object</code></b> : <div class="sub-desc">An object containing properties for StartDate, EndDate and IsAllDay
     * as defined in {@link Ext.calendar.data.EventMappings}.</div></li><ul></div>
     */
    setValue: function(v){
        if(!v) {
            return;
        }
        if(Ext.isArray(v)){
            this.setDT(v[0], 'start');
            this.setDT(v[1], 'end');
            this.allDay.setValue(!!v[2]);
        }
        else if(Ext.isDate(v)){
            this.setDT(v, 'start');
            this.setDT(v, 'end');
            this.allDay.setValue(false);
        }
        else if(v[Ext.calendar.data.EventMappings.StartDate.name]){ //object
            this.setDT(v[Ext.calendar.data.EventMappings.StartDate.name], 'start');
            if(!this.setDT(v[Ext.calendar.data.EventMappings.EndDate.name], 'end')){
                this.setDT(v[Ext.calendar.data.EventMappings.StartDate.name], 'end');
            }
           this.allDay.setValue(!!v[Ext.calendar.data.EventMappings.IsAllDay.name]);
           this.timeSet.setValue(v[Ext.calendar.data.EventMappings.TimeSet.name]);
        }
    },
    
    // private setValue helper
    setDT: function(dt, startend){
        if(dt && Ext.isDate(dt)){
            this[startend + 'Date'].setValue(dt);
            this[startend + 'Time'].setValue(Ext.Date.format(dt, this[startend + 'Time'].format));
            return true;
        }
    },
    
    // inherited docs
    isDirty: function(){
        var dirty = false;
        if(this.rendered && !this.disabled) {
            this.items.each(function(item){
                if (item.isDirty()) {
                    dirty = true;
                    return false;
                }
            });
        }
        return dirty;
    },
    
    // private
    onDisable : function(){
        this.delegateFn('disable');
    },
    
    // private
    onEnable : function(){
        this.delegateFn('enable');
    },
    
    // inherited docs
    reset : function(){
        this.delegateFn('reset');
    },
    
    // private
    delegateFn : function(fn){
        this.items.each(function(item){
            if (item[fn]) {
                item[fn]();
            }
        });
    },
    
    // private
    beforeDestroy: function(){
        Ext.destroy(this.fieldCt);
        this.callParent(arguments);
    },
    
    /**
     * @method getRawValue
     * @hide
     */
    getRawValue : Ext.emptyFn,
    /**
     * @method setRawValue
     * @hide
     */
    setRawValue : Ext.emptyFn,
    getSystemSet: function() {
		var result = false;
		Ext.Ajax.request({
			url : basePath + 'common/getFieldsData.action',
			async: false,
			params: {
				caller: 'AttendSystem',
				fields: 'as_amstarttime,as_amendtime,as_pmstarttime,as_pmendtime',
				condition:'1=1'
			},
			method : 'post',
			callback : function(opt, s, res){
				var r = new Ext.decode(res.responseText);
				if(r.exceptionInfo){
					showError(r.exceptionInfo);return;
				} else if(r.success && r.data){
					result = r.data;
				}
			}
		});
		return result;
	}
});