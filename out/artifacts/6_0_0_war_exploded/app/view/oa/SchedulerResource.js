Ext.require([
             'Sch.model.Resource',
             'Sch.model.Event',
             ]);
Ext.define('erp.view.oa.SchedulerResource',{ 
	extend: 'Ext.Viewport', 
	layout: 'fit', 
	hideBorders: true, 
	initComponent : function(){ 
		var me = this; 
		this.scheduler = this.createScheduler();
		this.populateScheduler();
		Ext.apply(me, { 
			items: [{ 
				layout: 'fit', 
				items: [this.scheduler]
			}] 
		}); 
		me.callParent(arguments); 
	},	    
	populateScheduler : function() {
		var me=this;
		Ext.Ajax.request({//拿到form的items
			url : basePath + 'oa/getSchedulerResourceData.action',
			params: {
				caller:caller
			},
			async:false,
			method : 'post',
			callback : function(options, success, response){
				var res = new Ext.decode(response.responseText);				
				me.scheduler.resourceStore.loadData(res.res.resourcedata);
				console.log(res);
				me.scheduler.eventStore.loadData(res.res.schdata);
			}
		});
		
	},

	createScheduler : function() {
		var me=this;
		var Configs=me.getConfigs(caller);
		Ext.define('Resource', {
			extend : 'Sch.model.Resource',
			fields:Configs.resourceFields,
			idProperty:'ID'
		});

		Ext.define('Event', {
			extend : 'Sch.model.Event',
			resourceIdField : "RESOURCEID",
			startDateField : "STARTDATE",
			endDateField : "ENDDATE",
			fields:Configs.SchFields
		});


		// Store holding all the resources
		var resourceStore = Ext.create('Sch.data.ResourceStore', {
			model : 'Resource'
		});
		var eventStore = Ext.create('Sch.data.EventStore', {
			model : 'Event'
		});
		var startDate = Ext.Date.parse(Ext.Date.format(new Date(),"Y-m-d"), "Y-m-d");
        var endDate=Ext.Date.add(startDate,Ext.Date.DAY,1);
		var g = Ext.create("Sch.panel.SchedulerGrid", {
			height : 300,
			width : 1000,
			allowOverlap : false,
			viewPreset : 'hourAndDay',
			startDate :startDate,
			endDate : endDate,        
			rowHeight : 25,
			loadMask : { store : eventStore },
			eventRenderer : function (event, resource, tplData, row, col, ds) {
				tplData.cls = 'evt-' + resource.get('Category');
				return event.get('Title');
			},

			resizeValidatorFn : function(resourceRecord, eventRecord, start, end) {
				if (eventRecord.get('Group') === 'min-one-day') {
					return Sch.util.Date.getDurationInDays(start, end) >= 1;
				}
				return true;
			},

			dndValidatorFn : function(dragEventRecords, targetRowRecord) {
				return targetRowRecord.get('Available');
			},

			// Setup your static columns
			columns :Configs.resourceColumns,

			           viewConfig :  {
			        	   getRowClass : function(resourceRecord) {
			        		   if (!resourceRecord.get('Available')) { 
			        			   return 'unavailable';
			        		   }
			        		   return '';
			        	   }
			           },

			           resourceStore : resourceStore,
			           eventStore : eventStore,
			           border : true,

			           tbar : [
			                   {
			                	   iconCls : 'icon-prev',
			                	   scale : 'medium',
			                	   handler : function() {
			                		   g.shiftPrevious();
			                	   }
			                   },
			                   '->',
			                   {
			                	   iconCls : 'icon-next',
			                	   scale : 'medium',
			                	   handler : function() {
			                		   g.shiftNext();
			                	   }
			                   }
			                   ],

			                   listeners : {
			                	   beforedragcreate : function(s, resource) {
			                		   if (!resource.get('Available')) {
			                			   //Ext.Msg.alert('Oops', "This machine is not available");
			                			   return false;
			                		   }
			                	   },

			                	   beforeeventdrag : function(s, r) {
			                		   return r.get('Group') !== 'non-movable';
			                	   },

			                	   beforeeventresize : function(s, r) {
			                		   if (r.get('Group') === 'non-resizable') {
			                			   Ext.Msg.alert('Oops', "DOH!");
			                			   return false;
			                		   }
			                	   }
			                   }
		});


		return g;
	},
	getConfigs:function (caller){
		var Object={};
		if(caller=='Meeting'){
			Object.resourceColumns=[{header : '会议室名称', sortable:true, width:150, dataIndex : 'MR_NAME', renderer : function(v, m, r) {
	        	   m.tdCls = r.get('Category');
	        	   return v;
	           }}];
			Object.resourceFields=[{
				name:'ID'
			},{
				name:'MR_NAME'
			},{
				name:'MR_SITE'
			}];
			Object.SchFields=[{
				name:'ME_NAME'
			},{
				name:'ME_CODE'
			}];
			Object.DataFields=[{
				name:'ME_TITLE'},{
				name:'ma_starttime'},{
				name:'ma_endtime'}];
		}else if(caller=='VehicleapplyToVehiclereturn!Deal'){
			Object.resourceColumns=[{header : '车牌号码', sortable:true, width:150, dataIndex : 'VA_CARD', renderer : function(v, m, r) {
	        	   m.tdCls = r.get('Category');
	        	   return v;
	           }}];
			Object.resourceFields=[{
				name:'ID'
			},{
				name:'VA_CARD'
			},{
				name:'VA_DRIVER'
			}];
			Object.SchFields=[{
				name:'VA_CARD'
			},{
				name:'VA_DRIVER'
			}];
			Object.DataFields=[{
				name:'VA_CARD'},{
				name:'vr_starttime'},{
				name:'vr_endtime'}];
		}
		return Object;
	}
});