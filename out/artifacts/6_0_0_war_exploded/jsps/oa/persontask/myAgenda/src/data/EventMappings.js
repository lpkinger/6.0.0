/**
 * @class Ext.calendar.data.EventMappings
 * @extends Object
 * A simple object that provides the field definitions for Event records so that they can be easily overridden.
 */
Ext.ns('Ext.calendar.data');

Ext.calendar.data.EventMappings = {
		 EventId: {
		        name: 'EventId',
		        mapping: 'Id',
		        type: 'int'
		    },
		    CalendarId: {
		        name: 'CalendarId',
		        mapping: 'Type',
		        type: 'int'
		    },
		    Title: {
		        name: 'Title',
		        mapping: 'Name',
		        type: 'string'
		    },
		    Shift: {
		        name: 'Shift',
		        mapping: 'ca_shift',
		        type: 'string'
		    },
		    StartDate: {
		        name: 'StartDate',
		        mapping: 'StartDate',
		        type: 'date',
		        dateFormat: 'c'
		    },
		    EndDate: {
		        name: 'EndDate',
		        mapping: 'EndDate',
		        type: 'date',
		        dateFormat: 'c'
		    },
		    Location: {
		        name: 'Location',
		        mapping: 'ca_location',
		        type: 'string'
		    },
		    Notes: {
		        name: 'Notes',
		        mapping: 'ca_notes',
		        type: 'string'
		    },
		    Description:{
		    	name:'description',
		    	mapping: 'description',
			    type: 'string'
		    },

		    IsAllDay: {
		        name: 'IsAllDay',
		        mapping:'responsible',   
		        type: 'boolean'
		    },
		    TimeSet:{
		    	name:'TimeSet',
		    	mapping: 'resizable',
		    	type:'string'
		    },
		    Recorder:{
		    	name:'Recorder',
		    	mapping:'recorder',
		    	type:'string'
		    },
		    Remark:{
		    	name:'Remark',
		    	mapping:'Remark',
		    	type:'string'
		    },
		    Reminder: {
		        name: 'Reminder',
		        mapping: 'ca_reminder',
		        type: 'string'
		    },
		    Url:{
		    	name:'Url',
		    	mapping:'sourcelink',
		    	type:'string'
		    },
		    IsNew: {
		        name: 'IsNew',
		        mapping: 'ca_isnew',
		        type: 'boolean'
		    },
		    IsContainWeekends:{
		      name:'IsContainWeekends',
		      mapping:'ca_iscontainweekends',
		      type:'boolean'  
		    },
		    WeekEnds:{
		      name:'WeekEnds',
		      mapping:'ca_weekends',
		      type:'int'    	
		    },
		     Shift:{
		      name:'Shift',
		      mapping:'ca_shift',
		      type:'string'    	
		    },
		    prjplanid:{
		    	name:'prjplanid',
		    	mapping:'prjplanid',
		    	type:'int'
		    },
		    tasktype:{
		    	name:'tasktype',
		    	mapping:'tasktype',
		    	type:'string'
		    },
		    manuallyscheduled:{//模板caller
		    	name:'manuallyscheduled',
		    	mapping:'manuallyscheduled',
		    	type:'string'
		    }
};
