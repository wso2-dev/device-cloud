/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

function formatDates() {
    $(".formatDate").each(function () {
        var timeStamp = $(this).html();

        var monthNames = [
            "Jan", "Feb", "Mar",
            "Apr", "May", "Jun", "Jul",
            "Aug", "Sept", "Oct",
            "Nov", "Dec"
        ];

        var date = new Date(parseInt(timeStamp));
        var day = date.getDate();
        var monthIndex = date.getMonth() + 1;
        if (monthIndex < 10)monthIndex = "0" + monthIndex;
        var year = date.getFullYear();

        var hours = date.getHours();
        var amPm = hours < 12 ? "AM" : "PM";
        if (amPm) hours -= 12;
        if (hours == 0)hours = 12;
        //+ ' @' + hours + ':' + date.getMinutes()+amPm
        $(this).html(day + '-' + monthNames[monthIndex - 1] + '-' + year);
    });
}

$(document).ready(function () {
    formatDates();
});