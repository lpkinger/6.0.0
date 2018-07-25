/**
 * @class Ext.calendar.data.EventMappings
 * @extends Object
 * A simple object that provides the field definitions for Event records so that they can be easily overridden.
 */
Ext.ns('Ext.calendar.data');

Ext.calendar.data.EventMappings = {
    EventId: {
        name: 'EventId',
        mapping: 'ca_eventid',
        type: 'int'
    },
    CalendarId: {
        name: 'CalendarId',
        mapping: 'ca_calendarid',
        type: 'int'
    },
    Title: {
        name: 'Title',
        mapping: 'ca_title',
        type: 'string'
    },
    Shift: {
        name: 'Shift',
        mapping: 'ca_shift',
        type: 'string'
    },
    StartDate: {
        name: 'StartDate',
        mapping: 'ca_startdate',
        type: 'date',
        dateFormat: 'c'
    },
    EndDate: {
        name: 'EndDate',
        mapping: 'ca_enddate',
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
    Url: {
        name: 'Url',
        mapping: 'ca_url',
        type: 'string'
    },
    IsAllDay: {
        name: 'IsAllDay',
        mapping: 'ca_isallday',
        type: 'boolean'
    },
    Reminder: {
        name: 'Reminder',
        mapping: 'ca_reminder',
        type: 'string'
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
    }
};

/**
 * @class Ext.calendar.data.EventMappings
 * @extends Object
 * A simple object that provides the field definitions for Event records so that they can be easily overridden.
 */
/** Ext.ns('Ext.calendar.data');

Ext.calendar.data.EventMappings = {
    EventId: {
        name: 'EventId',
        mapping: 'id',
        type: 'int'
    },
    CalendarId: {
        name: 'CalendarId',
        mapping: 'cid',
        type: 'int'
    },
    Title: {
        name: 'Title',
        mapping: 'title',
        type: 'string'
    },
    StartDate: {
        name: 'StartDate',
        mapping: 'start',
        type: 'date',
        dateFormat: 'c'
    },
    EndDate: {
        name: 'EndDate',
        mapping: 'end',
        type: 'date',
        dateFormat: 'c'
    },
    Location: {
        name: 'Location',
        mapping: 'loc',
        type: 'string'
    },
    Notes: {
        name: 'Notes',
        mapping: 'notes',
        type: 'string'
    },
    Url: {
        name: 'Url',
        mapping: 'url',
        type: 'string'
    },
    IsAllDay: {
        name: 'IsAllDay',
        mapping: 'ad',
        type: 'boolean'
    },
    Reminder: {
        name: 'Reminder',
        mapping: 'rem',
        type: 'string'
    },
    IsNew: {
        name: 'IsNew',
        mapping: 'n',
        type: 'boolean'
    }
};**/
