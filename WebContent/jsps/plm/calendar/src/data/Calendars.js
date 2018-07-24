Ext.define('Ext.calendar.data.Calendars', {
    statics: {
        getData: function(){
            return {
                "calendars":[{
                    "id":    1,
                    "title": "休假"
                },{
                    "id":    2,
                    "title": "上班"
                },{
                    "id":    3,
                    "title": "加班"
                }]
            };    
        }
    }
});