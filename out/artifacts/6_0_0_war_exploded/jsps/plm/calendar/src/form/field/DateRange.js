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
		toText: '至',
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
		singleLine: false,
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
		
		// private
		fieldLayout: {
			type: 'hbox',
			defaultMargins: { top: 0, right: 5, bottom: 5, left: 0 }
		},
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
						me.getDateSeparatorConfig(),
						me.getEndDateConfig(),
						me.getEndTimeConfig(),
						me.getAllDayConfig(),
					]
				},{
					xtype: 'container',
					layout: me.fieldLayout,
					items: [
					   me.getContainWeekendsConfig(),
					   me.getWeekendsConfig(),					 
					   //周六设置
					   //me.getSaturdayConfig(),
					  // me.getTosaturdayConfig(),
					  // me.getDateSeparatorConfig(),
					   //me.getEndTimeConfig(),
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
			me.containweekends=me.down('#'+me.id+'_containweekends');
			me.weekends=me.down('#'+me.id+'_weekends');
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
				me.getAllDayConfig(),
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
				//hideLabel: true,
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
			   // hideLabel: true,
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
	 
		getAllDayConfig: function() {
			return {
				xtype: 'checkbox',
				itemId: this.id + '-allday',
				hidden: this.showTimes === false || this.showAllDay === false,
				boxLabel: this.allDayText,
				margins: { top: 2, right: 5, bottom: 0, left: 0 },
				handler: this.onAllDayChange,
				scope: this
			};
		},
		getContainWeekendsConfig:function(){
			return {
				xtype: 'checkbox',
				itemId: this.id + '_containweekends',
				//hidden: this.showTimes === false || this.showAllDay === false,
			   // boxLabel: this.allDayText,
				boxLabel:'包含周末',
				margins: { top: 2, right: 5, bottom: 5, left: 2 },
				handler: this.onContainWeekendsChange,
				scope: this  
			}   
		},
		  getShiftConfig:function (){
                   return { 
						xtype: 'dbfindtrigger',
					    itemId: 'shift',
					    name: 'shift',
					    id:'shift',
					    fieldLabel:'班次设置',
					    width:180,
					    aftertrigger : function(t, r) {
							t.setValue(r.get('wd_code'));
						}
				   }
		  },
		  getWeekendsConfig:function(){
			return {
				xtype: 'combo',
				itemId: this.id + '_weekends',
				queryMode: 'local',
				fieldLabel:'设置规则',
				labelAlign:'right',
				labelWidth:60,
				width:190,
				listConfig:{
					  // emptyText: "未找到匹配值",
							maxHeight: 80
				},
				displayField:'weekendtype',
				valueField:'value',
				value:2,
				store:{
				fields:['weekendtype','value'],
				data:[{weekendtype:'单休',value:1},
					  {weekendtype:'双休',value:2},
					 ],       
				}, 
			   
				listeners: {
					'select': {
						fn: function(){                    
							 if (this.weekends.value !== undefined) {
								 this.weekends.setValue(this.weekends.value);
							 }
							 else {
								 this.weekends.setValue(0);
							 }
						 },            
						scope: this
					},
		  
				},
				margins: { top: 2, right: 5, bottom: 5, left: 2 },
				scope: this  
			}   
		},
	  /*  getSaturdayConfig: function() {
			  return {
			itemId: this.id + '_saturday',   
			xtype: 'timefield',
			fieldLabel: '周六设置',
			labelAlign:'right',
			listeners:{
			'beforerender':function(comp,opts){
			  comp.setVisible(false);
			}
			},
			labelWidth:60,
			width:150,
			minValue: '6:00 AM',
			maxValue: '8:00 PM',
			increment: 30,
			margins: { top: 2, right: 5, bottom: 5, left: 2 },
					}
		},*/
	   /* getTosaturdayConfig:function(){
		   return{
			itemId: this.id + '_tosaturday',   
			xtype: 'timefield',
			fieldLabel: '至&nbsp',
			listeners:{
			'beforerender':function(comp,opts){
			  comp.setVisible(false);
			}
			},
			labelSeparator:'',
			labelAlign:'right',
			labelWidth:15,
			width:105,
			minValue: '6:00 AM',
			maxValue: '8:00 PM',
			increment: 30,
			margins: { top: 2, right: 5, bottom: 5, left: 2 },
		   }
		},*/
		onContainWeekendsChange:function(chk, checked){  
			//this.weekends.reset();
			//对已设置的周末天数清0
			if(checked){
			this.weekends.setValue(0);    
			}
			else{
				this.weekends.setValue(2);      
			}
			 this.weekends.setVisible(!checked);
			//周六设置
		  /*  this.saturday.setVisible(!checked);
			this.tosaturday.setVisible(!checked);*/
		},
		onAllDayChange: function(chk, checked) {
			this.startTime.setVisible(!checked);
			this.endTime.setVisible(!checked);
		},
		//周六设置
		/**
		onweekendstypechange:function(data){
		if(data=='周六半天周日全天'){
		this.saturday.setVisible(true);
		this.tosaturday.setVisible(true);
		}else{
		 this.saturday.setVisible(false);
		  this.tosaturday.setVisible(false);
		}
		},
		**/
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
		onComboSelect: function(){
			
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
				this.containweekends.getValue(),
				this.getWeekendsValue(),
			];
		},
		getWeekendsValue:function(){
			var value=this.weekends.value;
			
			return value;
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
				this.containweekends.setValue(!!v[Ext.calendar.data.EventMappings.IsContainWeekends.name]);
				if (this.weekends.value !== undefined) {
					this.weekends.setValue(this.weekends.value);
				}
				else {
					this.weekends.setValue(0);
				}
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
		setRawValue : Ext.emptyFn
	});