Ext.define('erp.view.core.picker.TimePicker', {
	extend: 'Ext.Component',
	requires: ['Ext.XTemplate', 'Ext.Date', 'Ext.button.Button'],
	alias: 'widget.timepicker',
	alternateClassName: 'Ext.TimePicker',
	renderTpl: [
	            '<div id="{id}-bodyEl" class="{baseCls}-body">',
	            '<div class="{baseCls}-hours">',
	            '<div style="font-size:14px;text-align:center;font-weight:600">— 小时 —</div>', 
	            '<tpl for="hours">',
	            '<div class="{parent.baseCls}-item {parent.baseCls}-hour"><a href="#" hidefocus="on">{.}</a></div>',
	            '</tpl>',
	            '</div>',
	            '<div class="{baseCls}-minutes">', 
	            '<div style="font-size:14px;text-align:center;font-weight:600">— 分钟 —</div>',  
	            '<tpl for="minutes">',
	            '<div class="{parent.baseCls}-item {parent.baseCls}-minute"><a href="#" hidefocus="on">{.}</a></div>',
	            '</tpl>',
	            '</div>',
	            '<div class="' + Ext.baseCSSPrefix + 'clear"></div>',
	            '</div>',
	            '<tpl if="showButtons">',
	            '<div id="{id}-buttonsEl" class="{baseCls}-buttons"></div>',
	            '</tpl>'
	            ],
	            okText: '确认',
	            cancelText: '取消',
	            baseCls: Ext.baseCSSPrefix + 'timepicker',
	            showButtons: true,
	            smallCls: Ext.baseCSSPrefix + 'monthpicker-small',
	            totalYears: 10,
	            yearOffset: 5, 
	            monthOffset: 6,
	            width: 178,
	            hours:['0','1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23'],
	            minutes:['0','5','10','15','20','25','30','35','40','45','50','55'],
	            initComponent: function(){
	            	var me = this;
	            	me.selectedCls = me.baseCls + '-selected';
	            	me.addEvents(
	            			'cancelclick',
	            			'hourclick',
	            			'hourdblclick',
	            			'okclick',
	            			'select',
	            			'minuteclick',
	            			'minutedblclick'
	            	);
	            	if (me.small) {
	            		me.addCls(me.smallCls);
	            	}
	            	me.setValue(me.value);
	            	this.callParent();
	            },

	            // private, inherit docs
	            onRender: function(ct, position){
	            	var me = this,
	            	i = 0,
	            	months = [],
	            	shortName = Ext.Date.getShortMonthName,
	            	monthLen = me.monthOffset;

	            	for (; i < monthLen; ++i) {
	            		months.push(shortName(i), shortName(i + monthLen));
	            	}

	            	Ext.apply(me.renderData, {
	            		hours: me.hours,
	            		minutes: me.minutes,
	            		showButtons: me.showButtons
	            	});

	            	me.addChildEls('bodyEl', 'buttonsEl');
	            	me.callParent(arguments);
	            },

	            // private, inherit docs
	            afterRender: function(){
	            	var me = this,
	            	body = me.bodyEl,
	            	buttonsEl = me.buttonsEl;
	            	me.callParent();

	            	me.mon(body, 'click', me.onBodyClick, me);
	            	me.mon(body, 'dblclick', me.onBodyClick, me);

	            	// keep a reference to the year/month elements since we'll be re-using them
	            	me.minutes = body.select('.' + me.baseCls + '-minute a');
	            	me.hours = body.select('.' + me.baseCls + '-hour a');

	            	if (me.showButtons) {
	            		me.okBtn = Ext.create('Ext.button.Button', {
	            			text: me.okText,
	            			renderTo: buttonsEl,
	            			handler: me.onOkClick,
	            			scope: me
	            		});
	            		me.cancelBtn = Ext.create('Ext.button.Button', {
	            			text: me.cancelText,
	            			renderTo: buttonsEl,
	            			handler: me.onCancelClick,
	            			scope: me
	            		});
	            	}	
	            },
	            setValue: function(value){
	            	var me = this;
	            	if (!value) {
	            		me.value = [null, null];
	            	}else {
	            		var arrs=me.value.split(":"),
	            		hour=arrs[0],
	            		minute=arrs[1];
	            		hour =hour.charAt(0)=='0'? Number(hour.charAt(1)):Number(hour);
	            		minute =minute.charAt(0)=='0'? Number(minute.charAt(1)):Number(minute);
	            		me.value=[hour,minute];
	            	}
	            	if (me.rendered) {
	            		me.hours.removeCls(cls);
	            		me.minutes.removeCls(cls);
	            		me.minutes.each(function(el, all, index){	            			
	            			el.dom.innerHTML = minute;
	            			if (el.dom.innerHTML == minute) {
	            				el.dom.className = me.selectedCls;
	            			}
	            		});
	            		if (hour !== null) {
	            			me.hours.item(hour).addCls(me.selectedCls);
	            		}
	            	}

	            	return me;
	            },
	            getValue: function(){
	            	return this.value;
	            },
	            hasSelection: function(){
	            	var value = this.value;
	            	return value[0] !== null && value[1] !== null;
	            },
	            updateBody: function(){
	            	var me = this,
	            	hours = me.hours,
	            	minutes = me.minutes,
	            	cls = me.selectedCls,
	            	hour = me.value[0],
	            	minute = me.value[1];

	            	if (me.rendered) {
	            		hours.removeCls(cls);
	            		minutes.removeCls(cls);
	            		minutes.each(function(el, all, index){	            			
	            			el.dom.innerHTML = minute;
	            			if (el.dom.innerHTML == minute) {
	            				el.dom.className = cls;
	            			}
	            		});
	            		if (hour !== null) {
	            			hours.item(hour).addCls(cls);
	            		}
	            	}
	            },
	            getYear: function(defaultValue, offset) {
	            	var year = this.value[1];
	            	offset = offset || 0;
	            	return year === null ? defaultValue : year + offset;
	            },
	            onBodyClick: function(e, t) {
	            	var me = this,
	            	isDouble = e.type == 'dblclick';

	            	if (e.getTarget('.' + me.baseCls + '-hour')) {
	            		e.stopEvent();
	            		me.onHourClick(t, isDouble);
	            	} else if (e.getTarget('.' + me.baseCls + '-minute')) {
	            		e.stopEvent();
	            		me.onMinuteClick(t, isDouble);
	            	}
	            },
	            adjustYear: function(offset){
	            	if (typeof offset != 'number') {
	            		offset = this.totalYears;
	            	}
	            	this.activeYear += offset;
	            	this.updateBody();
	            },
	            onOkClick: function(){
	            	this.fireEvent('okclick', this, this.value);
	            },
	            onCancelClick: function(){
	            	this.fireEvent('cancelclick', this);
	            	this.hide();
	            },
	            onHourClick: function(target, isDouble){
	            	var me = this;
	            	me.value[0] = target.innerText;
	            	me.hours.removeCls(me.selectedCls);
	            	me.hours.each(function(el, all, index){
	            		if (el.dom.innerText == me.value[0] ) {
	            			el.dom.className = me.selectedCls;
	            		}
	            	});
	            	me.fireEvent('hour' + (isDouble ? 'dbl' : '') + 'click', me, me.value);
	            	me.fireEvent('select', me, me.value);
	            },
	            onMinuteClick: function(target, isDouble){
	            	var me = this;
	            	me.value[1]=target.innerText;
	            	me.minutes.removeCls(me.selectedCls);
	            	me.minutes.each(function(el, all, index){
	            		if (el.dom.innerText == me.value[1] ) {
	            			el.dom.className = me.selectedCls;
	            		}
	            	});
	            	me.fireEvent('minute' + (isDouble ? 'dbl' : '') + 'click', me, me.value);
	            	me.fireEvent('select', me, me.value);
	            },
	            resolveOffset: function(index, offset){
	            	if (index % 2 === 0) {
	            		return (index / 2);
	            	} else {
	            		return offset + Math.floor(index / 2);
	            	}
	            },
	            beforeDestroy: function(){
	            	var me = this;
	            	me.hours = me.minutes = null;
	            	Ext.destroyMembers(me, 'backRepeater', 'nextRepeater', 'okBtn', 'cancelBtn');
	            	me.callParent();
	            }
});