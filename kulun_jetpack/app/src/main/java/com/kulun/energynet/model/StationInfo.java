package com.kulun.energynet.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Orion on 2017/7/26.
 */
public class StationInfo implements Serializable,Comparable<StationInfo>{

    //"id": 99,
    //		"type": 1,
    //		"name": "西溪福堤1号",
    //		"address": "杭州市文二西路西溪湿地北门福堤1号",
    //		"phone": "13296723879",
    //		"contact": "胡志林",
    //		"channel": 15,
    //		"status": 0,
    //		"latitude": 30.278,
    //		"longitude": 120.07,
    //		"start_time": "2018-04-12T00:00:48+08:00",
    //		"end_time": "2018-04-12T23:59:48+08:00",
    //		"appointment": true,
    //		"waiting": 0,
    //		"distance": 6.32,
    //		"battery": 0

//    		"id": 97,
//                    "type": 1,
//                    "name": "西斗门",
//                    "address": "杭州市西湖区西斗门路9号福地2.0创业园内",
//                    "phone": "13296721573",
//                    "contact": "梁韩朝",
//                    "channel": 66,
//                    "status": 0,
//                    "latitude": 30.29048,
//                    "longitude": 120.11465,
//                    "start_time": "2018-04-12T01:56:00+08:00",
//                    "end_time": "2018-04-12T21:00:48+08:00",
//                    "appointment": true,
//                    "waiting": 0,
//                    "occupy": 0,
//                    "parkFee": "",
//                    "battery": 27
    //childSites
    private int id, type,channel,status,battery,waiting;
    private String name,address,phone,contact,start_time,end_time,parkFee;
    private double latitude,longitude, distances;
    private boolean appointment,listclick;
    private List<StationInfo> childSites;

    public String getParkFee() {
        return parkFee;
    }

    public void setParkFee(String parkFee) {
        this.parkFee = parkFee;
    }

    private boolean isreserverclick;

    public boolean isListclick() {
        return listclick;
    }

    public void setListclick(boolean listclick) {
        this.listclick = listclick;
    }

    public boolean isIsreserverclick() {
        return isreserverclick;
    }

    public void setIsreserverclick(boolean isreserverclick) {
        this.isreserverclick = isreserverclick;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getStatus() {
        return status;
    }

    public double getDistances() {
        return distances;
    }

    public void setDistances(double distances) {
        this.distances = distances;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getWaiting() {
        return waiting;
    }

    public void setWaiting(int waiting) {
        this.waiting = waiting;
    }

    public boolean isAppointment() {
        return appointment;
    }

    public void setAppointment(boolean appointment) {
        this.appointment = appointment;
    }

    public List<StationInfo> getChildSites() {
        return childSites;
    }

    public void setChildSites(List<StationInfo> childSites) {
        this.childSites = childSites;
    }

    public class ChildSites implements Serializable{
        /*{
			"id": 101,
			"type": 1,
			"name": "留下换电宝站",
			"address": "杭州市西湖区留和路129号安能物流",
			"phone": "13296723879",
			"contact": "胡志林",
			"channel": 0,
			"status": 0,
			"latitude": 30.23132,
			"longitude": 120.05118,
			"start_time": "2018-04-12T00:00:48+08:00",
			"end_time": "2018-04-12T17:59:48+08:00",
			"appointment": true,
			"waiting": 0,
			"occupy": 0,
			"parkFee": "0.3",
			"battery": 4
		}*/
        private int id, type, channel, status, waiting, occupy, battery;
        private double latitude, longitude;
        private String name, address, phone, contact,start_time, end_time, parkFee;
        private boolean appointment;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getChannel() {
            return channel;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getWaiting() {
            return waiting;
        }

        public void setWaiting(int waiting) {
            this.waiting = waiting;
        }

        public int getOccupy() {
            return occupy;
        }

        public void setOccupy(int occupy) {
            this.occupy = occupy;
        }

        public int getBattery() {
            return battery;
        }

        public void setBattery(int battery) {
            this.battery = battery;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getStart_time() {
            return start_time;
        }

        public void setStart_time(String start_time) {
            this.start_time = start_time;
        }

        public String getEnd_time() {
            return end_time;
        }

        public void setEnd_time(String end_time) {
            this.end_time = end_time;
        }

        public String getParkFee() {
            return parkFee;
        }

        public void setParkFee(String parkFee) {
            this.parkFee = parkFee;
        }

        public boolean isAppointment() {
            return appointment;
        }

        public void setAppointment(boolean appointment) {
            this.appointment = appointment;
        }
    }

    @Override
    public int compareTo(StationInfo o) {
        return (int) (this.distances-o.getDistances());
    }
}
