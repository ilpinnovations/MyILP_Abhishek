package com.ilp.ilpschedule.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ilp.ilpschedule.model.Contact;
import com.ilp.ilpschedule.model.Feedback;
import com.ilp.ilpschedule.model.Notification;
import com.ilp.ilpschedule.model.Slot;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = "DbHelper";
    private static int DB_VERSION = 6;
    private static String DB_NAME = "myilpschedule.db";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbStructure.ContactTable.COMMAND_CREATE);
        db.execSQL(DbStructure.LocationTable.COMMAND_CREATE);
        db.execSQL(DbStructure.NotificationTable.COMMAND_CREATE);
        db.execSQL(DbStructure.ScheduleTable.COMMAND_CREATE);
        db.execSQL(DbStructure.FeedbackTable.COMMAND_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL(DbStructure.ContactTable.COMMAND_DROP);
            db.execSQL(DbStructure.LocationTable.COMMAND_DROP);
            db.execSQL(DbStructure.NotificationTable.COMMAND_DROP);
            db.execSQL(DbStructure.ScheduleTable.COMMAND_DROP);
            db.execSQL(DbStructure.FeedbackTable.COMMAND_DROP);
            onCreate(db);
        }
    }

    public long addNotification(Notification notification) {
        long id = -1;
        ContentValues values = new ContentValues();
        values.put(DbStructure.NotificationTable.COLUMN_MSG,
                notification.getMsg());
        values.put(DbStructure.NotificationTable._ID, notification.getId());
        values.put(DbStructure.NotificationTable.COLUMN_TIME, notification
                .getDate().getTime());

        try {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(DbStructure.NotificationTable.TABLE_NAME, "", new String[]{});
            id = db.insertWithOnConflict(DbStructure.NotificationTable.TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return id;
    }

    public int addNotifications(List<Notification> notifications) {
        int added = 0;
        for (Notification notification : notifications) {
            if (notification != null && notification.isValid()
                    && addNotification(notification) != -1)
                added++;
        }
        return added;
    }

    public ArrayList<Notification> getNotifications() {
        ArrayList<Notification> notifications = new ArrayList<>();
        Notification notification;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(DbStructure.NotificationTable.TABLE_NAME,
                    null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    notification = new Notification();
                    notification
                            .setMsg(cursor.getString(cursor
                                    .getColumnIndexOrThrow(DbStructure.NotificationTable.COLUMN_MSG)));
                    notification
                            .setId(cursor.getLong(cursor
                                    .getColumnIndexOrThrow(DbStructure.NotificationTable._ID)));
                    notification
                            .setDate(new Date(
                                    cursor.getLong(cursor
                                            .getColumnIndexOrThrow(DbStructure.NotificationTable.COLUMN_TIME))));
                    notifications.add(notification);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        return notifications;
    }

    public long addContact(Contact contact) {
        long id = 0;
        ContentValues values = new ContentValues();
        values.put(DbStructure.ContactTable.COLUMN_TITLE, contact.getTitle());
        values.put(DbStructure.ContactTable.COLUMN_NUMBER, contact.getNumber());
        try {
            SQLiteDatabase db = getWritableDatabase();
            id = db.insertWithOnConflict(DbStructure.ContactTable.TABLE_NAME, null,
                    values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }

    public int addContacts(List<Contact> contacts) {
        int added = 0;
        for (Contact contact : contacts) {
            if (contact != null && contact.isValid()
                    && addContact(contact) != -1)
                added++;
        }
        return added;
    }

    public List<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(DbStructure.ContactTable.TABLE_NAME, null,
                    null, null, null, null, null);
            Contact contact;
            if (cursor.moveToFirst()) {
                do {
                    contact = new Contact();
                    contact.setId(cursor.getLong(cursor
                            .getColumnIndexOrThrow(DbStructure.ContactTable._ID)));
                    contact.setNumber(cursor.getString(cursor
                            .getColumnIndexOrThrow(DbStructure.ContactTable.COLUMN_NUMBER)));
                    contact.setTitle(cursor.getString(cursor
                            .getColumnIndexOrThrow(DbStructure.ContactTable.COLUMN_TITLE)));
                    contacts.add(contact);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return contacts;
    }


    public int addSlots(List<Slot> slots) {
        int added = 0;
        for (Slot slot : slots) {
            if (slot != null && slot.isValid() && addSlot(slot) != -1)
                added++;
        }
        return added;
    }

    public long addSlot(Slot slot) {
        long id = -1;
        ContentValues values = new ContentValues();
        values.put(DbStructure.ScheduleTable.COLUMN_BATCH, slot.getBatch());
        values.put(DbStructure.ScheduleTable.COLUMN_DATE, slot.getDate()
                .getTime());
        values.put(DbStructure.ScheduleTable.COLUMN_SLOT, slot.getSlot());
        values.put(DbStructure.ScheduleTable.COLUMN_COURSE, slot.getCourse());
        values.put(DbStructure.ScheduleTable.COLUMN_FACULTY, slot.getFaculty());
        values.put(DbStructure.ScheduleTable.COLUMN_ROOM, slot.getRoom());
        try {
            SQLiteDatabase db = getWritableDatabase();
            id = db.insertWithOnConflict(DbStructure.ScheduleTable.TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return id;
    }

    public Slot getSlot(long id) {
        Slot slot = null;
        try {
            SQLiteDatabase db = getWritableDatabase();
            Cursor cursor = db.query(DbStructure.ScheduleTable.TABLE_NAME, null,
                    "_id=?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {

                slot = new Slot();
                slot.setBatch(cursor.getString(cursor
                        .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_BATCH)));
                slot.setCourse(cursor.getString(cursor
                        .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_COURSE)));
                slot.setDate(new Date(
                        cursor.getLong(cursor
                                .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_DATE))));
                slot.setFaculty(cursor.getString(cursor
                        .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_FACULTY)));
                slot.setRoom(cursor.getString(cursor
                        .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_ROOM)));
                slot.setSlot(cursor.getString(cursor
                        .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_SLOT)));
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return slot;
    }

    public List<Slot> getSchedule(Date date, String batch) {
        ArrayList<Slot> slots = new ArrayList<>();
        String where = DbStructure.ScheduleTable.COLUMN_DATE + DbConstants.EQUALS + DbConstants.QUESTION_MARK + DbConstants.AND + DbStructure.ScheduleTable.COLUMN_BATCH + DbConstants.EQUALS + DbConstants.QUESTION_MARK;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(DbStructure.ScheduleTable.TABLE_NAME, null,
                    where, new String[]{String.valueOf(date.getTime()), batch},
                    null, null, DbStructure.ScheduleTable.COLUMN_SLOT);

            Slot slot;
            if (cursor.moveToFirst()) {
                do {
                    slot = new Slot();
                    slot.setId(cursor.getLong(cursor
                            .getColumnIndexOrThrow(DbStructure.ScheduleTable._ID)));
                    slot.setBatch(cursor.getString(cursor
                            .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_BATCH)));
                    slot.setCourse(cursor.getString(cursor
                            .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_COURSE)));
                    slot.setDate(new Date(
                            cursor.getLong(cursor
                                    .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_DATE))));
                    slot.setFaculty(cursor.getString(cursor
                            .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_FACULTY)));
                    slot.setSlot(cursor.getString(cursor
                            .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_SLOT)));
                    slot.setRoom(cursor.getString(cursor
                            .getColumnIndexOrThrow(DbStructure.ScheduleTable.COLUMN_ROOM)));
                    slots.add(slot);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        Log.d(TAG, "date=" + date.getTime() + "batch=" + batch + "\n" + slots);
        return slots;
    }

    public long addFeedback(Feedback feedback) {
        long id = -1;
        ContentValues values = new ContentValues();
        values.put(DbStructure.FeedbackTable.COLUMN_COMMENT,
                feedback.getComment());
        values.put(DbStructure.FeedbackTable.COLUMN_RATING,
                feedback.getRating());
        values.put(DbStructure.FeedbackTable.COLUMN_SLOT_REF,
                feedback.getSlot_id());
        try {
            SQLiteDatabase db = getWritableDatabase();
            id = db.insertWithOnConflict(DbStructure.FeedbackTable.TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return id;
    }

    public Feedback getFeedbackBySlotId(long id) {
        Feedback feedback = null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(
                    DbStructure.FeedbackTable.TABLE_NAME,
                    null,
                    DbStructure.FeedbackTable.COLUMN_SLOT_REF + DbConstants.EQUALS + DbConstants.QUESTION_MARK,
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {
                feedback = new Feedback();
                feedback.setId(cursor.getLong(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable._ID)));
                feedback.setComment(cursor.getString(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable.COLUMN_COMMENT)));
                feedback.setRating(cursor.getFloat(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable.COLUMN_RATING)));
                feedback.setSlot_id(cursor.getLong(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable.COLUMN_SLOT_REF)));
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return feedback;
    }

    public Feedback getFeedback(long id) {
        Feedback feedback = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(
                    DbStructure.FeedbackTable.TABLE_NAME,
                    null,
                    DbStructure.FeedbackTable._ID + DbConstants.EQUALS + DbConstants.QUESTION_MARK,
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {
                feedback = new Feedback();
                feedback.setId(cursor.getLong(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable._ID)));
                feedback.setComment(cursor.getString(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable.COLUMN_COMMENT)));
                feedback.setRating(cursor.getFloat(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable.COLUMN_RATING)));
                feedback.setSlot_id(cursor.getLong(cursor
                        .getColumnIndexOrThrow(DbStructure.FeedbackTable.COLUMN_SLOT_REF)));
            }
            cursor.close();
            db.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return feedback;
    }

}
