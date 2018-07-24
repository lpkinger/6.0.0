Ext.define('erp.view.core.picker.MultiHourPicker', {
	extend: 'Ext.Component',
	requires: ['Ext.XTemplate', 'Ext.Date', 'Ext.button.Button'],
	alias: 'widget.multihourpicker',
	alternateClassName: 'Ext.TimePicker',
	renderTpl: [
	            '<div id="{id}-bodyEl" class="{baseCls}-body">',
	            '<div class="{baseCls}-hours" style="width:238px;height:100px">',
	            '<div style="font-size:14px;text-align:center;font-weight:600">— 小时 —</div>', 
	            '<tpl for="hours">',
	            '<div class="{parent.baseCls}-item {parent.baseCls}-hour"><a href="#" hidefocus="on">{.}</a></div>',
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
	            height:131,
	            hours:['0','1','2','3','4','5','6','7','8','9','10','11','12','13','14','15','16','17','18','19','20','21','22','23'],	           
	            selectedHours: null,
	            constructor: function(args){
	                this.callParent([Ext.applyIf(args||{}, {
	                	selectedHours: {},          	
	                })]);   
	            },
	            initComponent: function(){
	            	var me = this;
	            	me.selectedCls = me.baseCls + '-selected';     	
	            	me.addEvents(
	            			'cancelclick',
	            			'okclick'
	            	);
	            	if (me.small) {
	            		me.addCls(me.smallCls);
	            	}
	            	this.callParent();
	            },
	            onRender: function(ct, position){
	            	var me = this;
	            	Ext.apply(me.renderData, {
	            		hours: me.hours,
	            		showButtons: me.showButtons
	            	});
	            	me.addChildEls('bodyEl', 'buttonsEl');
	            	me.callParent(arguments);
	            },

	            afterRender: function(){
	            	var me = this,
	            	body = me.bodyEl,
	            	buttonsEl = me.buttonsEl;
	            	me.callParent();

	            	me.mon(body, 'click', me.onBodyClick, me);
	            	me.mon(body, 'dblclick', me.onBodyClick, me);
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
	            	me.setValue(me.value);
	            },
	            setValue: function(value){
	            	var me = this;
	            	if (!value) {
	            		me.value =null;
	            	}else{
		            	Ext.Array.each(value.split(","), function(h){
		            		/*if(h.indexOf(":")>0) h=h.split(":")[0];//针对之前设置的时间格式08:00
		            		if(h.startsWith("0")) h=h.substr(1,1);*/
		            		me.selectedHours[h] = h;
						});
	            	}
	            	me.higlighHours();
	            	if (me.rendered) {me.higlighHours();}
	            	return me;
	            },
	            getValue: function(){
	            	return this.value;
	            },
	            getSelectedHours: function(){
	                var hours = [];
	                Ext.iterate(this.selectedHours, function(key, val){
	                	hours.push(val);
	                });
	                hours.sort(function(a,b){return a-b}); 
	                return hours;
	            },
	            onBodyClick: function(e, t) {
	            	var me = this,
	            	isDouble = e.type == 'dblclick';
	            	if (e.getTarget('.' + me.baseCls + '-hour')&&t.className!="x-timepicker-item x-timepicker-hour") {
	            		e.stopEvent();
	            		me.onHourClick(t, isDouble);
	            	}
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
	            	var selected=target.innerText;
	            	if(this.selectedHours[selected])
	                     {
	            		delete this.selectedHours[selected];}
	                else {
	                	this.selectedHours[selected] = selected;}
	            	this.higlighHours();
	            	var hours=this.getSelectedHours();
	            	this.value=hours.join(",");
	          },
	            higlighHours: function(){
	                var me = this; 
	            	me.hours.each(function(el, all, index){
	            		var hour=el.dom.innerText;
	            		if (me.selectedHours[hour]) {
	            			el.dom.className = me.selectedCls;
	            		}else el.dom.className ='';
	            	});	            
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
	            	me.hoursnull;
	            	Ext.destroyMembers(me, 'backRepeater', 'nextRepeater', 'okBtn', 'cancelBtn');
	            	me.callParent();
	            }
});