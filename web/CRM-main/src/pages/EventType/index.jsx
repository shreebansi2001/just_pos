import { useEffect, useState } from "react";
import AddEventtype from "../../partials/modal/Eventtype/AddEventtype";
import {
  GetEventType,
  DeleteEventType,
  SearchEventType,
} from "@/services/apiServices";
import Swal from "sweetalert2";
import { Button } from "@/components/ui/button";
import { usePermission } from "@/hooks/usePermission";
import { FormattedMessage, useIntl } from "react-intl";
import { Pencil, Trash2, ChevronLeft, ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";

const EventType = () => {
  const { view, add, edit, delete: canDelete } = usePermission("Crm Event Type");
  const [isEventTypeModalOpen, setIsEventTypeModalOpen] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [tableData, setTableData] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [page, setPage] = useState(1);
  const perPage = 10;
  const intl = useIntl();

  const Id = localStorage.getItem("mainId");
  const lang = localStorage.getItem("lang") || "en";

  const getEventTypeByLang = (event) => {
    if (!event) return "-";
    switch (lang) {
      case "hi": return event.nameHindi || event.nameEnglish || "-";
      case "gu": return event.nameGujarati || event.nameEnglish || "-";
      default: return event.nameEnglish || "-";
    }
  };

  const formatEventData = (events) =>
    events.map((event, index) => ({
      sr_no: index + 1,
      event_type: getEventTypeByLang(event),
      eventid: event.id,
      nameEnglish: event.nameEnglish,
      nameHindi: event.nameHindi,
      nameGujarati: event.nameGujarati,
    }));

  const FetchEventType = () => {
    GetEventType(Id)
      .then((res) => {
        const data = res?.data?.data?.["EventTypes Details"];
        setTableData(data ? formatEventData(data) : []);
      })
      .catch(() => setTableData([]));
  };

  useEffect(() => {
    FetchEventType();
  }, [lang]);

  useEffect(() => {
    const handler = setTimeout(() => {
      if (!searchQuery.trim()) {
        FetchEventType();
        return;
      }
      SearchEventType(searchQuery, Id)
        .then(({ data: { data } }) => {
          const list = data?.["EventTypes Details"];
          setTableData(list ? formatEventData(list) : []);
        })
        .catch(() => setTableData([]));
    }, 500);
    return () => clearTimeout(handler);
  }, [searchQuery, lang]);

  const DeleteEventtype = (eventid) => {
    Swal.fire({
      title: intl.formatMessage({ id: "USER.MASTER.DELETE_CONFIRM_TITLE", defaultMessage: "Are you sure?" }),
      text: intl.formatMessage({ id: "USER.MASTER.DELETE_CONFIRM_TEXT", defaultMessage: "You won't be able to revert this!" }),
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: intl.formatMessage({ id: "USER.MASTER.DELETE_CONFIRM_BUTTON", defaultMessage: "Yes, delete it!" }),
      cancelButtonText: intl.formatMessage({ id: "USER.MASTER.CANCEL_BUTTON", defaultMessage: "Cancel" }),
    }).then((result) => {
      if (result.isConfirmed) {
        DeleteEventType(eventid)
          .then((response) => {
            if (response && (response.success || response.data.success === true)) {
              FetchEventType();
              Swal.fire({
                title: intl.formatMessage({ id: "USER.MASTER.DELETE_SUCCESS_TITLE", defaultMessage: "Removed!" }),
                text: intl.formatMessage({ id: "USER.MASTER.EVENT_TYPE_DELETE_SUCCESS", defaultMessage: "Event type has been removed successfully." }),
                icon: "success",
                timer: 1500,
                showConfirmButton: false,
              });
            }
          })
          .catch((err) => console.error("Error deleting Event type:", err));
      }
    });
  };

  const handleEdit = (event) => {
    setSelectedEvent(event);
    setIsEventTypeModalOpen(true);
  };

  const handleModalClose = () => {
    setIsEventTypeModalOpen(false);
    setSelectedEvent(null);
  };

  const totalPages = Math.ceil(tableData.length / perPage);
  const pagedData = tableData.slice((page - 1) * perPage, page * perPage);

  return (
    <div className="min-h-screen">
      <main className="px-6 space-y-5">

        {/* Header */}
        <div className="flex items-center justify-between mb-5">
          <h1 className="text-2xl font-semibold text-gray-900">
            <FormattedMessage id="USER.MASTER.EVENT_TYPE_MASTER" defaultMessage="Event Master" />
          </h1>
          
        {add && (
            <Button
              size="md"
              onClick={() => { setSelectedEvent(null); setIsEventTypeModalOpen(true); }}
              className="bg-primary"
              type="button"
            >
              Create New
              <ChevronRight className="w-4 h-4 ml-1" />
            </Button>
          )}
       
         
        </div>

        {/* Search */}
        <div className="relative w-fit">
          <i className="ki-filled ki-magnifier leading-none text-md text-primary absolute top-1/2 start-0 -translate-y-1/2 ms-3"></i>
          <input
            className="input pl-8"
            placeholder={intl.formatMessage({ id: "USER.MASTER.SEARCH_EVENT_TYPE", defaultMessage: "Search Event Type" })}
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <AddEventtype
          isModalOpen={isEventTypeModalOpen}
          setIsModalOpen={handleModalClose}
          refreshData={FetchEventType}
          selectedEvent={selectedEvent}
        />

        {/* Table */}
        <div className="rounded-2xl bg-white border border-gray-100 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-100 text-xs text-muted-foreground font-medium">
                  <th className="px-5 py-3 text-left">SR NO.</th>
                  <th className="px-3 py-3 text-left">EVENT TYPE</th>
                  <th className="px-3 py-3 text-left">ACTIONS</th>
                </tr>
              </thead>
              <tbody>
                {pagedData.length > 0 ? (
                  pagedData.map((event) => (
                    <tr key={event.eventid} className="border-b border-gray-50 hover:bg-gray-50">
                      <td className="px-5 py-3">{event.sr_no}</td>
                      <td className="px-3 py-3">{event.event_type}</td>
                      <td className="px-3 py-3">
                        <div className="flex items-center gap-2">
                          {edit && (
                            <button
                              onClick={() => handleEdit(event)}
                              className="p-1 rounded hover:bg-gray-100"
                            >
                              <Pencil className="w-4 h-4" />
                            </button>
                          )}
                          {canDelete  && (
                          <button
                            onClick={() => DeleteEventtype(event.eventid)}
                            className="p-1 rounded hover:bg-red-50 text-red-500"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={3} className="px-5 py-6 text-center text-gray-400 text-sm">
                      No records found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="px-5 py-3 flex items-center justify-between border-t border-gray-100">
            <span className="text-xs text-muted-foreground">
              Showing {tableData.length === 0 ? 0 : (page - 1) * perPage + 1}–
              {Math.min(page * perPage, tableData.length)} of {tableData.length} records
            </span>
            <div className="flex items-center gap-1">
              <button
                onClick={() => setPage((p) => Math.max(1, p - 1))}
                disabled={page === 1}
                className="p-1 rounded hover:bg-gray-100 disabled:opacity-30"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              {Array.from({ length: totalPages }, (_, i) => (
                <button
                  key={i}
                  onClick={() => setPage(i + 1)}
                  className={cn(
                    "w-6 h-6 text-xs rounded flex items-center justify-center transition-colors",
                    page === i + 1 ? "bg-primary text-white" : "hover:bg-gray-100 text-gray-600"
                  )}
                >
                  {i + 1}
                </button>
              ))}
              <button
                onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                disabled={page === totalPages || totalPages === 0}
                className="p-1 rounded hover:bg-gray-100 disabled:opacity-30"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

      </main>
    </div>
  );
};

export default EventType;