Ext.define('Ext.calendar.data.Calendars', {
    statics: {
        getData: function(){
            return {
                "calendars":[{
                    "id":    1,
                    "title": "未完成"
                },{
                    "id":    2,
                    "title": "已完成"
                },{
                    "id":    3,
                    "title": "已取消"
                }]
            };    
        }
    }
});