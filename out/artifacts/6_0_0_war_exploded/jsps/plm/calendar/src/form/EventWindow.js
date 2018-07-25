/**
 * @class Ext.calendar.form.EventWindow
 * @extends Ext.Window
 * <p>A custom window containing a basic edit form used for quick editing of events.</p>
 * <p>This window also provides custom events specific to the calendar so that other calendar components can be easily
 * notified when an event has been edited via this component.</p>
 * @constructor
 * @param {Object} config The config object
 */
Ext.define('Ext.calendar.form.EventWindow', {
	extend: 'Ext.window.Window',
	alias: 'widget.eventeditwindow',

	requires: [
	           'Ext.form.Panel',
	           'Ext.calendar.data.EventModel',
	           'Ext.calendar.data.EventMappings'
	           ],

	           constructor: function(config) {
	        	   var formPanelCfg = {
	        			   xtype: 'form',
	        			   fieldDefaults: {
	        				   msgTarget: 'side',
	        				   labelWidth: 65
	        			   },
	        			   frame: false,
	        			   bodyStyle: 'background:transparent;padding:5px 10px 10px;',
	        			   bodyBorder: false,
	        			   border: false,
	        			   items: [{
	        				   itemId: 'title',
	        				   name: Ext.calendar.data.EventMappings.Title.name,
	        				   fieldLabel: '标题',
	        				   xtype: 'textfield',
	        				   // allowBlank: false,
	        				   anchor: '100%'
	        			   },
	        			   {
	        				   xtype: 'daterangefield',
	        				   itemId: 'date-range',
	        				   name: 'dates',
	        				   anchor: '100%',
	        				   fieldLabel: '时间'
	        			   },{	
	        				   fieldLabel: '班次',
	        				   xtype: 'dbfindtrigger',
	        				   name: Ext.calendar.data.EventMappings.Shift.name,
	        				   fieldLabel:'班次设置',
	        				   winWidth:800,
	        				   winHeight:500,
	        				   anchor: '35%',
	        				   listeners:{
	        					   aftertrigger : function(t, r) {
		        					   t.setValue(r.get('wd_code'));
		        				   }
	        				   }
	        				   

	        			   }]
	        	   };

	        	   if (config.calendarStore) {
	        		   this.calendarStore = config.calendarStore;
	        		   delete config.calendarStore;

	        		   formPanelCfg.items.push({
	        			   xtype: 'calendarpicker',
	        			   itemId: 'calendar',
	        			   name: Ext.calendar.data.EventMappings.CalendarId.name,
	        			   anchor: '35%',
	        			   store: this.calendarStore
	        		   });
	        	   }
	        	   this.callParent([Ext.apply({
	        		   titleTextAdd: '添加事件',
	        		   titleTextEdit: '编辑事件',
	        		   width: 750,
	        		   autocreate: true,
	        		   border: true,
	        		   closeAction: 'hide',
	        		   modal: false,
	        		   resizable: false,
	        		   buttonAlign: 'left',
	        		   savingMessage: 'Saving changes...',
	        		   deletingMessage: 'Deleting event...',
	        		   layout: 'fit',
	        		   fbar: [{
	        			   xtype: 'tbtext',
	        			   text: '<a href="#" id="tblink">编辑明细...</a>'
	        		   },
	        		   '->',
	        		   {
	        			   itemId: 'delete-btn',
	        			   text: '删除事件',
	        			   disabled: false,
	        			   handler: this.onDelete,
	        			   scope: this,
	        			   minWidth: 150,
	        			   hideMode: 'offsets'
	        		   },
	        		   {
	        			   text: '保存',
	        			   disabled: false,
	        			   handler: this.onSave,
	        			   scope: this
	        		   },
	        		   {
	        			   text: '取消',
	        			   disabled: false,
	        			   handler: this.onCancel,
	        			   scope: this
	        		   }],
	        		   items: formPanelCfg
	        	   },
	        	   config)]);
	           },
	           newId: 10000,
	           initComponent: function() {
	        	   this.callParent();
	        	   this.formPanel = this.items.items[0];
	        	   this.addEvents({
	        		   eventadd: true,
	        		   eventupdate: true,
	        		   eventdelete: true,
	        		   eventcancel: true,
	        		   editdetails: true
	        	   });
	           },

	           // private
	           afterRender: function() {
	        	   this.callParent();

	        	   this.el.addCls('ext-cal-event-win');

	        	   Ext.get('tblink').on('click', this.onEditDetailsClick, this);

	        	   this.titleField = this.down('#title');
	        	   this.dateRangeField = this.down('#date-range');
	        	   this.calendarField = this.down('#calendar');
	        	   this.deleteButton = this.down('#delete-btn');

	        	   this.titleField.isValid = function() {
	        		   var valid = this.getValue().length > 0;
	        		   if (!valid) {
	        			   this.focus();
	        		   }
	        		   return valid;
	        	   };
	           },
	           onEditDetailsClick: function(e){
	        	   e.stopEvent();
	        	   this.updateRecord(this.activeRecord, true);
	        	   this.fireEvent('editdetails', this, this.activeRecord, this.animateTarget);
	           },

	           /**
	            * Shows the window, rendering it first if necessary, or activates it and brings it to front if hidden.
	            * @param {Ext.data.Record/Object} o Either a {@link Ext.data.Record} if showing the form
	            * for an existing event in edit mode, or a plain object containing a StartDate property (and 
	            * optionally an EndDate property) for showing the form in add mode. 
	            * @param {String/Element} animateTarget (optional) The target element or id from which the window should
	            * animate while opening (defaults to null with no animation)
	            * @return {Ext.Window} this
	            */
	           show: function(o, animateTarget) {
	        	   // Work around the CSS day cell height hack needed for initial render in IE8/strict:
	        	   var me = this,
	        	   anim = (Ext.isIE8 && Ext.isStrict) ? null: animateTarget,
	        			   M = Ext.calendar.data.EventMappings;

	        	   this.callParent([anim, function(){
	        		   me.titleField.focus(true, 100);
	        	   }]);

	        	   this.deleteButton[o.data && o.data[M.EventId.name] ? 'show': 'hide']();

	        	   var rec,
	        	   f = this.formPanel.form;

	        	   if (o.data) {
	        		   rec = o;
	        		   this.setTitle(rec.phantom ? this.titleTextAdd : this.titleTextEdit);
	        		   f.loadRecord(rec);
	        	   }
	        	   else {
	        		   this.setTitle(this.titleTextAdd);

	        		   var start = o[M.StartDate.name],
	        		   end = o[M.EndDate.name] || Ext.calendar.util.Date.add(start, {hours: 1});

	        		   rec = Ext.create('Ext.calendar.data.EventModel');
	        		   rec.data[M.StartDate.name] = start;
	        		   rec.data[M.EndDate.name] = end;
	        		   rec.data[M.IsAllDay.name] = !!o[M.IsAllDay.name] || start.getDate() != Ext.calendar.util.Date.add(end, {millis: 1}).getDate();

	        		   f.reset();
	        		   f.loadRecord(rec);
	        	   }

	        	   if (this.calendarStore) {
	        		   this.calendarField.setValue(rec.data[M.CalendarId.name]);
	        	   }
	        	   this.dateRangeField.setValue(rec.data);
	        	   this.activeRecord = rec;

	        	   return this;
	           },
	           roundTime: function(dt, incr) {
	        	   incr = incr || 15;
	        	   var m = parseInt(dt.getMinutes(), 10);
	        	   return dt.add('mi', incr - (m % incr));
	           },
	           onCancel: function() {
	        	   this.cleanup(true);
	        	   this.fireEvent('eventcancel', this);
	           },
	           cleanup: function(hide) {
	        	   if (this.activeRecord && this.activeRecord.dirty) {
	        		   this.activeRecord.reject();
	        	   }
	        	   delete this.activeRecord;

	        	   if (hide === true) {
	        		   // Work around the CSS day cell height hack needed for initial render in IE8/strict:
	        		   //var anim = afterDelete || (Ext.isIE8 && Ext.isStrict) ? null : this.animateTarget;
	        		   this.hide();
	        	   }
	           },
	           updateRecord: function(record, keepEditing) {
	        	   var fields = record.fields,
	        	   values = this.formPanel.getForm().getValues(),
	        	   name,
	        	   M = Ext.calendar.data.EventMappings,
	        	   obj = {};
	        	   fields.each(function(f) {
	        		   name = f.name;
	        		   if (name in values) {
	        			   obj[name] = values[name];
	        		   }
	        	   });
	        	   var dates = this.dateRangeField.getValue();
	        	   obj[M.StartDate.name] = dates[0];
	        	   obj[M.EndDate.name] = dates[1];
	        	   obj[M.IsAllDay.name] = dates[2];
	        	   //周末设置
	        	   obj[M.IsContainWeekends.name]=dates[3];
	        	   obj[M.WeekEnds.name]=dates[4];
	        	   record.beginEdit();
	        	   record.set(obj);

	        	   if (!keepEditing) {
	        		   record.endEdit();
	        	   }
	        	   return this;
	           },
	           onSave: function(){
	        	   if(!this.formPanel.form.isValid()){
	        		   return;
	        	   }
	        	   if(!this.updateRecord(this.activeRecord)){
	        		   this.onCancel();
	        		   return;
	        	   }
	        	   this.fireEvent(this.activeRecord.phantom ? 'eventadd' : 'eventupdate', this, this.activeRecord, this.animateTarget);
	        	   this.activeRecord.commit();
	           },
	           onDelete: function(){
	        	   this.fireEvent('eventdelete', this, this.activeRecord, this.animateTarget);
	           }
});